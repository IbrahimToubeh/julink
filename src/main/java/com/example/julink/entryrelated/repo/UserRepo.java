package com.example.julink.entryrelated.repo;

import com.example.julink.entryrelated.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<Users, Long> {
    Optional<Users> findByUsername(String username);

    Optional<Users> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<Users> findByActiveFalseAndDeactivatedAtBefore(LocalDateTime dateTime);

}