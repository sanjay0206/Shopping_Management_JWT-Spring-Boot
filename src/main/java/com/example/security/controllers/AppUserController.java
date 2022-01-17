package com.example.security.controllers;

import com.example.security.services.AppUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class AppUserController {
    private final AppUserService appUserService;
    private static final Map<String, Object> RESPONSE = new LinkedHashMap<>();

    @Autowired
    public AppUserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @PostMapping(path = "/users/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> addNewUser(@RequestBody Map<String, Object> request) {
        String message = appUserService.addUser(request);
        log.info("Message: {}", message);
        if (message.equals("Success")) {
            RESPONSE.put("status", message);
            RESPONSE.put("message", "User is added successfully");
        } else {
            RESPONSE.put("status", "Failure");
            RESPONSE.put("message", message);
        }
        return new ResponseEntity<>(RESPONSE, HttpStatus.OK);
    }

    @PutMapping(path = "/users/update")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<Map<String, Object>> updateUser(@RequestBody Map<String, Object> request) {
        log.info("Your request: {}", request);
        String message = appUserService.updateUser(request);
        log.info("Message: {}", message);
        if (message.equals("Success")) {
            RESPONSE.put("status", message);
            RESPONSE.put("message", "User is updated");
        } else {
            RESPONSE.put("status", "Failure");
            RESPONSE.put("message", message);
        }
        return new ResponseEntity<>(RESPONSE, HttpStatus.OK);
    }

    @DeleteMapping(path = "/users/delete/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteBook(@PathVariable("userId") Long userId) {
        String message = appUserService.deleteUser(userId);
        log.info("Message: {}", message);
        if (message.equals("Success")) {
            RESPONSE.put("status", message);
            RESPONSE.put("message", "User is deleted");
        } else {
            RESPONSE.put("status", "Failure");
            RESPONSE.put("message", message);
        }
        return new ResponseEntity<>(RESPONSE, HttpStatus.OK);
    }
}
