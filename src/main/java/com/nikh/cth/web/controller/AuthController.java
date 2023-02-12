package com.nikh.cth.web.controller;

import com.nikh.cth.bean.request.AuthRequest;
import com.nikh.cth.bean.response.AuthResponse;
import com.nikh.cth.error.ApiException;
import com.nikh.cth.service.TokenManagerService;
import com.nikh.cth.utils.ExceptionCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class AuthController {

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenManagerService tokenManager;

    @PostMapping("/auth")
    public ResponseEntity<?> createToken(@RequestBody AuthRequest
                                                request) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(),
                            request.getPassword())
            );
        } catch (DisabledException e) {
            throw new ApiException("USER_DISABLED", e, ExceptionCode.UNAUTHORIZED);
        } catch (BadCredentialsException e) {
            throw new ApiException("INVALID_CREDENTIALS", e, ExceptionCode.UNAUTHORIZED);
        }
        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        final String jwtToken = tokenManager.generateJwtToken(userDetails);
        return ResponseEntity.ok(new AuthResponse(jwtToken));
    }
}
