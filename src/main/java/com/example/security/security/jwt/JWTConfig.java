package com.example.security.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "application.jwt")
public class JWTConfig {
    private String secretKey;
    private String tokenPrefix;
    private Integer tokenExpiration;

    public byte[] getSecretKeyForSigning() {
        return secretKey.getBytes();
    }

    public JWTConfig(String secretKey, String tokenPrefix, Integer tokenExpiration) {
        this.secretKey = secretKey;
        this.tokenPrefix = tokenPrefix;
        this.tokenExpiration = tokenExpiration;
    }

    public JWTConfig() {
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getTokenPrefix() {
        return tokenPrefix;
    }

    public void setTokenPrefix(String tokenPrefix) {
        this.tokenPrefix = tokenPrefix;
    }

    public Integer getTokenExpiration() {
        return tokenExpiration;
    }

    public void setTokenExpiration(Integer tokenExpiration) {
        this.tokenExpiration = tokenExpiration;
    }

    @Override
    public String toString() {
        return "JwtConfig{" +
                "secretKey='" + secretKey + '\'' +
                ", tokenPrefix='" + tokenPrefix + '\'' +
                ", tokenExpirationAfterDays=" + tokenExpiration +
                '}';
    }
}
