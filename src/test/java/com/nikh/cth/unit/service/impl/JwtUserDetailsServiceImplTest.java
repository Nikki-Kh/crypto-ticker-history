package com.nikh.cth.unit.service.impl;

import com.nikh.cth.dao.UserDao;
import com.nikh.cth.service.impl.JwtUserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = {JwtUserDetailsServiceImpl.class, UserDao.class})
class JwtUserDetailsServiceImplTest {

    @Autowired
    JwtUserDetailsServiceImpl jwtUserDetailsService;

    @MockBean
    UserDao userDao;


    @Test
    void loadUserByUsernameSuccessful() {
        when(userDao.getUserByUserName(eq("admin"))).thenReturn(Optional.of("admin password"));
        when(userDao.getUserByUserName(eq("guest"))).thenReturn(Optional.empty());

        var result1 = jwtUserDetailsService.loadUserByUsername("admin");
        assertEquals("admin", result1.getUsername());
        assertEquals("password", result1.getPassword());

        assertThrows(UsernameNotFoundException.class,
                () -> jwtUserDetailsService.loadUserByUsername("guest"),
                "User not found with username: guest");
    }

}