package com.example.julink.filter;

import com.example.julink.entryrelated.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserCleanupService {

    private final UserRepo userRepo;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void deleteInactiveUsersOlderThan30Days() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        var usersToDelete = userRepo.findByActiveFalseAndDeactivatedAtBefore(thirtyDaysAgo);

        if (!usersToDelete.isEmpty()) {
            userRepo.deleteAll(usersToDelete);
        }
    }
}
