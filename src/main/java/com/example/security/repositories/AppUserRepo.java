package com.example.security.repositories;

import com.example.security.entities.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AppUserRepo extends JpaRepository<AppUser, Long> {
    @Query(value = "select * from users where username=?1", nativeQuery = true)
    Optional<AppUser> findByUsername(String username);

    @Query(value = "select email_id from users where email_id=?1", nativeQuery = true)
    Optional<AppUser> findByEmailId(String emailId);



}
