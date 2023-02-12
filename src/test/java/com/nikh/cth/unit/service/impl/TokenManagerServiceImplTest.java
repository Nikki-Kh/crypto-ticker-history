package com.nikh.cth.unit.service.impl;

import com.nikh.cth.error.ApiException;
import com.nikh.cth.service.impl.JwtUserDetailsServiceImpl;
import com.nikh.cth.service.impl.TokenManagerServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.ContextConfiguration;


import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = {TokenManagerServiceImpl.class, JwtUserDetailsServiceImpl.class})
class TokenManagerServiceImplTest {

    @MockBean
    JwtUserDetailsServiceImpl userDetailsService;

    @Autowired
    TokenManagerServiceImpl tokenManagerService;

    @Test
    void validateJwtTokenSuccess() throws ApiException {
        var userDetails = new User("admin", "password", Collections.emptyList());
        var token = tokenManagerService.generateJwtToken(userDetails);
        when(userDetailsService.loadUserByUsername(eq("admin"))).thenReturn(userDetails);
        var validationResult = tokenManagerService.validateJwtToken(token);
        assertEquals(userDetails, validationResult);
    }

    @Test
    void validateJwtTokenVerificationException() throws ApiException {
        var userDetails = new User("admin", "password", Collections.emptyList());
        var token = tokenManagerService.generateJwtToken(userDetails);

        assertThrows(ApiException.class,
                ()-> tokenManagerService.validateJwtToken(token.concat("_garbage_suffix")),
                "JWT Verification format error");
    }

    @Test
    void validateJwtTokenExpireException() throws ApiException, InterruptedException {
        var userDetails = new User("admin", "password", Collections.emptyList());
        var token = tokenManagerService.generateJwtToken(userDetails);
        Thread.sleep(3000);
        assertThrows(ApiException.class,
                () -> tokenManagerService.validateJwtToken(token),
                "Token Expired");
    }
}