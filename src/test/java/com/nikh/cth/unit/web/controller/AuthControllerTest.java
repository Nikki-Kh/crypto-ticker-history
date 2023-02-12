package com.nikh.cth.unit.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nikh.cth.bean.request.AuthRequest;
import com.nikh.cth.bean.response.AuthResponse;
import com.nikh.cth.bean.response.ErrorResponse;
import com.nikh.cth.service.TokenManagerService;
import com.nikh.cth.service.impl.JwtUserDetailsServiceImpl;
import com.nikh.cth.service.impl.TokenManagerServiceImpl;
import com.nikh.cth.web.advice.ExceptionHandlerAdvice;
import com.nikh.cth.web.controller.AuthController;
import com.nikh.cth.web.entrypoint.AppAuthenticationEntryPoint;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = {JwtUserDetailsServiceImpl.class, AuthController.class,
        AuthenticationManager.class, TokenManagerServiceImpl.class, TestSecurityConfig.class,
        AppAuthenticationEntryPoint.class, ExceptionHandlerAdvice.class
})
public class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    JwtUserDetailsServiceImpl jwtUserDetailsService;

    @MockBean
    AuthenticationManager authenticationManager;

    @MockBean
    TokenManagerService tokenManagerService;

    ObjectMapper mapper = new ObjectMapper();

    @BeforeAll
    void beforeAll() {
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    void testAuthenticationSuccess() throws Exception {
        var authRequest = new AuthRequest("admin", "password");
        var authResponse = new AuthResponse("test");

        var user = new User("admin", "password", Collections.emptyList());
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(jwtUserDetailsService.loadUserByUsername(eq("admin")))
                .thenReturn(user);
        when(tokenManagerService.generateJwtToken(eq(user))).thenReturn("test");
        mockMvc.perform(post("/auth")
                .contentType("application/json")
                .content(mapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(authResponse)));
    }


    @Test
    void testAuthenticationFails() throws Exception {
        var authRequest = new AuthRequest("admin", "password");
        var errorResponse = ErrorResponse.builder()
                .code(5)
                .url("/auth")
                .message("INVALID_CREDENTIALS")
                .build();

        var user = new User("admin", "password", Collections.emptyList());
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("TEST BAD CREDS"));
        mockMvc.perform(post("/auth")
                .contentType("application/json")
                .content(mapper.writeValueAsString(authRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(mapper.writeValueAsString(errorResponse)));
    }

}
