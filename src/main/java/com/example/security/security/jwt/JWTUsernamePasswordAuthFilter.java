package com.example.security.security.jwt;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class JWTUsernamePasswordAuthFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTConfig jwtConfig;
    private final JWTService jwtService;
    private static final ObjectMapper MAPPER = new ObjectMapper();
    public static final String COOKIE_NAME = "access-token";

    public JWTUsernamePasswordAuthFilter(AuthenticationManager authenticationManager,
                                         JWTConfig jwtConfig,
                                         JWTService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtConfig = jwtConfig;
        this.jwtService = jwtService;
    }

    @Bean
    public JWTUsernamePasswordAuthFilter getJWTAuthenticationFilter() {
        final JWTUsernamePasswordAuthFilter filter =
                new JWTUsernamePasswordAuthFilter
                        (authenticationManager, jwtConfig, jwtService);
        filter.setFilterProcessesUrl("/api/v1/auth/signIn");
        return filter;
    }

    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response)
            throws AuthenticationException {
        Cookie[] cookies = request.getCookies();
        log.info("Incoming cookies: " + Arrays.toString(cookies));
        String payload = new BufferedReader(new InputStreamReader(request.getInputStream()))
                .lines()
                .collect(Collectors.joining(System.lineSeparator()));
        Map<String, Object> json = MAPPER.readValue
                (payload, new TypeReference<Map<String, Object>>() {});
        String username = (String) json.get("username");
        String password = (String) json.get("password");
        log.info("Username is: {}", username);
        log.info("Password is: {}", password);
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
        return authenticationManager.authenticate(authentication);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException {
        log.info("Auth result: {}", authResult);
        String token = jwtService.createJWT(authResult);
        Cookie cookie = new Cookie(COOKIE_NAME, token);
//        cookie.setSecure(true); // to work only in https
//        cookie.setHttpOnly(true);
//        cookie.setPath("/");
        cookie.setMaxAge(3600 * 24 * jwtConfig.getTokenExpiration());

        log.info("Cookie: {}", cookie.getValue());
        response.addHeader(HttpHeaders.COOKIE, cookie.getValue());
        response.addCookie(cookie);

        // add token to the header
        String JWT = jwtConfig.getTokenPrefix() + token;
        response.addHeader(HttpHeaders.AUTHORIZATION, JWT);
        // send JWT token to response body
        Map<String, Object> accessToken = new LinkedHashMap<>();
        accessToken.put("access_token", token);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        MAPPER.writeValue(response.getOutputStream(), accessToken);
    }
}
