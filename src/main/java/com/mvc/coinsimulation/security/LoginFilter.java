package com.mvc.coinsimulation.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        // 인증을 위한 사용자 입력 값(username, password 등) 추출 및 처리
        String username = request.getParameter("providerId");

        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, null);
        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    /*@Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) {
//        super.successfulAuthentication(request, response, chain, authResult);
        // 추가적인 성공 처리 로직
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
//        super.unsuccessfulAuthentication(request, response, failed);
        // 추가적인 실패 처리 로직
    }*/
}
