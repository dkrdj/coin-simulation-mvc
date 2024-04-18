package com.mvc.coinsimulation.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvc.coinsimulation.dto.common.UserDto;
import com.mvc.coinsimulation.dto.request.LoginRequest;
import com.mvc.coinsimulation.dto.response.LoginResponse;
import com.mvc.coinsimulation.entity.User;
import com.mvc.coinsimulation.exception.LoginException;
import com.mvc.coinsimulation.repository.postgres.UserRepository;
import com.mvc.coinsimulation.util.S3Utils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Optional;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final S3Utils s3Utils;
    private final ObjectMapper snakeOM;
    private final UserRepository userRepository;

    public LoginFilter(S3Utils s3Utils, ObjectMapper snakeOM, UserRepository userRepository) {
        this.s3Utils = s3Utils;
        this.snakeOM = snakeOM;
        this.userRepository = userRepository;
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        LoginRequest loginRequest = null;
        try {
            loginRequest = snakeOM.readValue(request.getInputStream(), LoginRequest.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (loginRequest == null || !StringUtils.hasText(loginRequest.getNickname()) || !StringUtils.hasText(loginRequest.getProfile())) {
            throw new LoginException();
        }
        UserDetailsCustom userDetails;
        try {
            userDetails = new UserDetailsCustom(getUserDto(loginRequest));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        UserDetailsCustom userDetails = (UserDetailsCustom) authResult.getPrincipal();
        LoginResponse loginResponse = new LoginResponse(userDetails.getUsername(), userDetails.getProfile());
        String jsonStr = null;
        try {
            jsonStr = snakeOM.writeValueAsString(loginResponse);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        request.getSession().setAttribute("user", userDetails.getUserId());
        response.getWriter().print(jsonStr);
    }

    public UserDto getUserDto(LoginRequest loginRequest) throws IOException {
        Optional<User> userOptional = userRepository.findByProviderId(loginRequest.getProviderId());
        if (userOptional.isEmpty()) {
            return userRepository.save(User.builder()
                            .providerId(loginRequest.getProviderId())
                            .profile(s3Utils.uploadFromUrl(loginRequest.getProfile(), loginRequest.getProviderId()))
                            .nickname(loginRequest.getNickname())
                            .role("USER")
                            .cash(30000000d)
                            .build())
                    .toDto();
        }
        return userOptional.get().toDto();
    }
}
