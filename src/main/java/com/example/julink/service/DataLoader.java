package com.example.julink.service;

import com.example.julink.bulk.entity.College;
import com.example.julink.bulk.repositories.CollegeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final CollegeRepository collegeRepo;

    @Override
    public void run(String... args) throws Exception {
        if (collegeRepo.count() == 0) {
            collegeRepo.save(new College("Engineering"));
            collegeRepo.save(new College("KASIT"));
            collegeRepo.save(new College("Medical School"));
            collegeRepo.save(new College("Literature"));
            collegeRepo.save(new College("Physical Education"));

        }
    }
}
