package com.example.security.controllers;

import com.example.security.security.jwt.JWTUsernamePasswordAuthFilter;
import com.example.security.services.AppUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AppUserService appUserService;
    private static final Map<String, Object> RESPONSE = new LinkedHashMap<>();

    @Autowired
    public AuthController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @PostMapping(path = "/signUp")
    public ResponseEntity<Map<String, Object>> signUp(@RequestBody Map<String, Object> request) {
        String role = (String) request.get("role");
        if (!role.equals("ADMIN")) {
            String message = appUserService.addUser(request);
            log.info("Message: {}", message);
            if (message.equals("Success")) {
                RESPONSE.put("status", message);
                RESPONSE.put("message", "User is added successfully");
            }
            RESPONSE.put("status", "Failure");
            RESPONSE.put("message", message);
        } else {
            RESPONSE.put("status", "Failure");
            RESPONSE.put("message", "You can't signUp as ADMIN");
        }
        return new ResponseEntity<>(RESPONSE, HttpStatus.OK);
    }

    @PostMapping(path = "/signOut")
    public ResponseEntity<Map<String, Object>> signOut(HttpServletRequest httpServletRequest,
                                                       HttpServletResponse httpServletResponse) {
        SecurityContextHolder.clearContext();
        Optional<Cookie> cookieAuth = Arrays.stream(httpServletRequest.getCookies())
                .filter(cookie -> Objects.equals(JWTUsernamePasswordAuthFilter.COOKIE_NAME, cookie.getName()))
                .findAny();

        if (cookieAuth.isPresent()) {
            Cookie deleteServletCookie = new Cookie(cookieAuth.get().getName(), null);
            deleteServletCookie.setMaxAge(0);
            httpServletResponse.addCookie(deleteServletCookie);
            log.info("Cookie after signOut: {}", deleteServletCookie.getValue());
            httpServletResponse.addHeader(HttpHeaders.COOKIE, deleteServletCookie.getValue());
            RESPONSE.put("status", "Success");
            RESPONSE.put("message", "User is logged out successfully");
        }
        return new ResponseEntity<>(RESPONSE, HttpStatus.OK);
    }
}
