package com.nikh.cth.service;

import com.nikh.cth.error.ApiException;
import org.springframework.security.core.userdetails.UserDetails;

public interface TokenManagerService {

     String generateJwtToken(UserDetails details);

     UserDetails validateJwtToken(String token) throws ApiException;

}
