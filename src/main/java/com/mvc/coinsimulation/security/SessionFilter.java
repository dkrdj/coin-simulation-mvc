package com.mvc.coinsimulation.security;

import com.mvc.coinsimulation.dto.common.UserDto;
import com.mvc.coinsimulation.exception.NoUserException;
import com.mvc.coinsimulation.repository.postgres.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class SessionFilter extends OncePerRequestFilter {
    private final UserRepository userRepository;

    public SessionFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Object userAttribute = request.getSession().getAttribute("user");
        if (userAttribute == null) {
            filterChain.doFilter(request, response);
            return;
        }
        Long userId = Long.parseLong(String.valueOf(userAttribute));
        UserDto userDto = userRepository.findById(userId).orElseThrow(NoUserException::new).toDto();
        UserDetailsCustom userDetails = new UserDetailsCustom(userDto);
        System.out.println(userDto);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}
