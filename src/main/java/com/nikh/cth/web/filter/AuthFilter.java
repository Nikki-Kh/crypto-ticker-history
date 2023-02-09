package com.nikh.cth.web.filter;

import com.nikh.cth.error.ApiException;
import com.nikh.cth.error.ExceptionCode;
import com.nikh.cth.service.TokenManagerService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthFilter extends OncePerRequestFilter {

    @Autowired
    private TokenManagerService tokenManager;

    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var tokenHeader = request.getHeader("Authorization");
        if (tokenHeader == null || !tokenHeader.startsWith("Bearer ") ) {
            throw new ApiException("Not Authorized", ExceptionCode.UNAUTHORIZED);
        }
        var token = tokenHeader.substring(7);
        tokenManager.validateJwtToken(token);
        filterChain.doFilter(request, response);
    }
}
