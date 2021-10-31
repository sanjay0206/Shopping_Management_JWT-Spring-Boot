package com.example.security.services;

import com.example.security.entities.AppUser;
import com.example.security.repositories.AppUserRepo;
import com.example.security.security.ApplicationUserRole;
import com.example.security.security.PasswordConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class AppUserService  implements UserDetailsService {

    private final AppUserRepo appUserRepo;
    private final PasswordConfig passwordConfig;

    @Autowired
    public AppUserService(AppUserRepo appUserRepo, PasswordConfig passwordConfig) {
        this.appUserRepo = appUserRepo;
        this.passwordConfig = passwordConfig;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<AppUser> appUser = appUserRepo.findByUsername(username);
        if (!appUser.isPresent()) {
            throw new UsernameNotFoundException(username);
        }
        AppUser user = appUser.get();
        String appUsername = user.getUsername();
        String appUserPassword = user.getPassword();
        String encodedPassword = passwordConfig.passwordEncoder().encode(appUserPassword);
        ApplicationUserRole appUserRole = user.getRole();
        Set<SimpleGrantedAuthority> authorities = appUserRole.getGrantedAuthorities();
        return User
                .withUsername(appUsername)
                .password(encodedPassword)
                .authorities(authorities)
                .build();
    }

    public String addUser(Map<String, Object> request) {
        String username = (String) request.get("username");
        String password = (String) request.get("password");
        String emailId = (String) request.get("emailId");
        ApplicationUserRole role = ApplicationUserRole.valueOf((String) request.get("role"));
        Optional<AppUser> user = appUserRepo.findByUsername(username);
        if (user.isPresent()) {
            log.error("User with username {} is already present", username);
            return "User with username " + username + " is already present";
        }
        AppUser appUser = new AppUser();
        appUser.setUsername(username);
        appUser.setPassword(password);
        appUser.setEmailId(emailId);
        appUser.setRole(role);
        appUserRepo.save(appUser);
        return "Success";
    }

    @Transactional
    public String updateUser(Map<String, Object> request) {
        try {
            String userIdObj = request.get("user_id").toString();
            if (userIdObj ==  null) {
                return "Please provide the user id to update";
            }
            String username = request.get("username") == null ? "NA" :
                    (request.get("username").equals("")) ? "NA" : request.get("username").toString();
            String emailId = request.get("emailId") == null ? "NA" :
                    (request.get("emailId").equals("")) ? "NA" : request.get("emailId").toString();
            String password =  request.get("password") == null ? "NA" : request.get("password").toString();
            long userId = Long.parseLong(userIdObj);

            Optional<AppUser> appUser = appUserRepo.findById(userId);
            if (!appUser.isPresent()) {
                return "User with id " + userId + " is not found";
            } else {
                String existingUsername = appUser.get().getUsername();
                if (!username.equals("NA") && !Objects.equals(username, existingUsername)) {
                    Optional<AppUser> usernameOptional =
                            appUserRepo.findByUsername(username);
                    if(usernameOptional.isPresent()) {
                        return "username already taken";
                    }
                    appUser.get().setUsername(username);
                }

                String existingEmailId = appUser.get().getEmailId();
                if (!emailId.equals("NA") && !Objects.equals(emailId, existingEmailId)) {
                    Optional<AppUser> emailIdOptional =
                            appUserRepo.findByEmailId(emailId);
                    if(emailIdOptional.isPresent()) {
                        return "email already taken";
                    }
                    appUser.get().setEmailId(emailId);
                }

                String existingPassword = appUser.get().getPassword();
                if (!password.equals("NA") && !Objects.equals(password, existingPassword)) {
                    appUser.get().setPassword(password);
                }
            }
        } catch (Exception e) {
            return e.getMessage();
        }
        return "Success";
    }

    public String deleteUser(Long userId) {
        boolean exists = appUserRepo.existsById(userId);
        if (!exists) {
            log.error("User with id {} does not exists", userId);
            return "User with id " + userId + " does  ot exists";
        }
        appUserRepo.deleteById(userId);
        return "Success";
    }
}
