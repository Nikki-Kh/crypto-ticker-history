package com.nikh.cth.service.impl;

import com.nikh.cth.dao.UserDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class JwtUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var result = userDao.getUserByUserName(username);
        return result.map(it -> {
                    var creds = it.split(StringUtils.SPACE);
                    return new User(creds[0], creds[1], Collections.emptyList());
                }).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

}
