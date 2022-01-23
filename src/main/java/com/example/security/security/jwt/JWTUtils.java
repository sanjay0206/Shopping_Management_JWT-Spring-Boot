package com.example.security.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class JWTUtils {
    private final JWTConfig jwtConfig;

    @Autowired
    public JWTUtils(JWTConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    public String createJWT(Authentication authResult) {
        List<String> authorities = authResult.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        String subject = authResult.getName();
        Date now = new Date();
        Date validity = new Date(now.getTime() + (3600000 * 24) * jwtConfig.getTokenExpiration());
        byte[] secretKey = jwtConfig.getSecretKeyForSigning();
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        String JWTToken = JWT.create()
                .withSubject(subject)
                .withClaim("authorities",  authorities)
                .withIssuedAt(now)
                .withExpiresAt(validity)
                .sign(algorithm);
        log.info("JWT token is: {}", JWTToken);
        return JWTToken;
    }

    public DecodedJWT decodeJWT(String token) {
        byte[] secretKey = jwtConfig.getSecretKeyForSigning();
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(token);
        log.info("Decoded JWT: " + decodedJWT);
        return decodedJWT;
    }
}
