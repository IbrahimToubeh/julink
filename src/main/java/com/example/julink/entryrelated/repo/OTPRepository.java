package com.example.julink.entryrelated.repo;

import com.example.julink.entryrelated.entity.OTPClass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OTPRepository extends JpaRepository<OTPClass, Long> {
    Optional<OTPClass> findByEmail(String email);
}
