package com.nikh.cth.service.impl;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.JsonParser;
import com.nikh.cth.error.ApiException;
import com.nikh.cth.utils.Consts;
import com.nikh.cth.utils.ExceptionCode;
import com.nikh.cth.service.TokenManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;

import com.auth0.jwt.JWT;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class TokenManagerServiceImpl implements TokenManagerService {

    @Value("${spring.security.token.validity:3600}")
    private long TOKEN_VALIDITY;
    @Value("${spring.security.secret}")
    private String jwtSecret;

    @Autowired
    UserDetailsService userDetailsService;



    @Override
    public String generateJwtToken(UserDetails details) {
        final Algorithm algorithm = Algorithm.
                HMAC256(jwtSecret);
        return JWT.create()
                .withClaim(Consts.NAME_JSON_FIELD, details.getUsername())
                .withExpiresAt(Date.from(Instant.now().plus(TOKEN_VALIDITY, ChronoUnit.SECONDS)))
                .sign(algorithm);
    }

    @Override
    public UserDetails validateJwtToken(String token) throws ApiException {
        try {
            final Algorithm algorithm = Algorithm.
                    HMAC256(jwtSecret);
            final JWTVerifier verifier = JWT.require(algorithm)
                    .build();
            final DecodedJWT jwt = verifier.verify(token);
            var payload = new String(Base64Utils.decode(jwt.getPayload().getBytes(UTF_8)));
            var jsonPayload = JsonParser.parseString(payload).getAsJsonObject();
            if (!jsonPayload.has(Consts.NAME_JSON_FIELD)) {
                throw new JWTVerificationException("Wrong token format");
            }
            var name = jsonPayload.get(Consts.NAME_JSON_FIELD).getAsString();
            return userDetailsService.loadUserByUsername(name);
        }
        catch (final TokenExpiredException e) {
            throw new ApiException( "Token Expired", e, ExceptionCode.TOKEN_EXPIRED);
        } catch (final JWTVerificationException ex) {
            throw new ApiException("JWT Verification format error", ex, ExceptionCode.WRONG_TOKEN_FORMAT);
        } catch (final Exception ex) {
            throw new ApiException("JWT Decode unknown error", ex, ExceptionCode.UNKNOWN);
        }

    }

}
