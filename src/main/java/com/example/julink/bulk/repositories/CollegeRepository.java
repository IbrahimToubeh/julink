package com.example.julink.bulk.repositories;


import com.example.julink.bulk.entity.College;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollegeRepository extends JpaRepository<College, Long> {
    // If you need to search by name in the future
    College findByName(String name);
}
