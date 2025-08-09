package com.example.julink.bulk.repositories;


import com.example.julink.bulk.entity.Like;
import com.example.julink.bulk.entity.Post;
import com.example.julink.entryrelated.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Optional;


@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    // Check if a user already liked a post
    Optional<Like> findByPostAndUser(Post post, Users user);

    // Get likes for a specific post
    List<Like> findByPost(Post post);



    // Likes in the past week (used for trending posts)
    List<Like> findByPostAndCreatedAtAfter(Post post, LocalDateTime dateTime);


    Optional<Like> findByUserAndPost(Users user, Post post);
    long countByPost(Post post);

}
