package com.example.julink.bulk.repositories;


import com.example.julink.bulk.entity.Like;
import com.example.julink.bulk.entity.Post;
import com.example.julink.entryrelated.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Optional;


@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByPostAndUser(Post post, Users user);

    List<Like> findByPost(Post post);


    Optional<Like> findByUserAndPost(Users user, Post post);

    Page<Like> findByUserId(Long userId, Pageable pageable);


    long countByPost(Post post);

}
