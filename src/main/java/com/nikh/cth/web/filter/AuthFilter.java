package com.nikh.cth.web.filter;


import com.nikh.cth.error.ApiException;
import com.nikh.cth.service.TokenManagerService;
import com.nikh.cth.utils.Consts;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Component
public class AuthFilter extends OncePerRequestFilter {

    @Autowired
    private TokenManagerService tokenManager;

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var tokenHeader = request.getHeader(Consts.AUTHORIZATION_HEADER);
        if (tokenHeader != null && tokenHeader.startsWith(Consts.BEARER_PREFIX.concat(StringUtils.SPACE)) ) {
            var token = tokenHeader.split(StringUtils.SPACE)[1];
            try {
                var userDetails = tokenManager.validateJwtToken(token);
                var authToken = new UsernamePasswordAuthenticationToken(userDetails, null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
            catch (ApiException e) {
                resolver.resolveException(request, response, null, e);
            }
        }
        filterChain.doFilter(request, response);
    }

}
