package com.example.security.security.jwt;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class JWTTokenVerifier extends OncePerRequestFilter {
    private final JWTConfig jwtConfig;
    private final JWTUtils jwtService;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Autowired
    public JWTTokenVerifier(JWTConfig jwtConfig, JWTUtils jwtService) {
        this.jwtConfig = jwtConfig;
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String tokenPrefix = jwtConfig.getTokenPrefix();
        if (Strings.isNullOrEmpty(authorizationHeader) ||
                !authorizationHeader.startsWith(tokenPrefix)) {
            filterChain.doFilter(request, response);
            return;
        }
        // if the token is correct then decode the JWT token.
        try {
            String token = authorizationHeader.replace(tokenPrefix, "");
            Optional<Cookie> cookieAuth = Arrays.stream(request.getCookies())
                    .filter(cookie -> Objects.equals(JWTUsernamePasswordAuthFilter.COOKIE_NAME, cookie.getName()))
                    .findAny();

            if (cookieAuth.isPresent() && cookieAuth.get().getValue().equals(token)) {
                if (!Strings.isNullOrEmpty(token)) {
                    DecodedJWT decodedJWT = jwtService.decodeJWT(token);
                    String username = decodedJWT.getSubject();

                    Map<String, Claim> claims = decodedJWT.getClaims();
                    List<String> authorities = decodedJWT.getClaims().get("authorities").asList(String.class);
                    Set<SimpleGrantedAuthority> grantedAuthorities = authorities.stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toSet());
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            grantedAuthorities);
                    log.info("JWT Claims is: {}", claims);
                    log.info("Granted authorities is: {}", grantedAuthorities);
                    log.info("Authentication: {}", authentication);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
            // if the claims is correct then move to the next filter.
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            Map<String, String> error = new LinkedHashMap<>();
            error.put("status", "Failure");
            error.put("message", e.getMessage());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            MAPPER.writeValue(response.getOutputStream(), error);
        }
    }
}
