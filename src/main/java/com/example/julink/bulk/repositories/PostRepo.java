package com.example.julink.bulk.repositories;

import com.example.julink.bulk.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.util.List;

public interface PostRepo extends JpaRepository<Post, Long> {

    List<Post> findByAuthorId(Long authorId);



    @Query("""
    SELECT p FROM Post p
    JOIN p.taggedColleges c
    WHERE c.id = :collegeId AND p.createdAt >= :oneWeekAgo
    ORDER BY (SELECT COUNT(l) FROM Like l WHERE l.post = p AND l.createdAt >= :oneWeekAgo) DESC
""")
    Page<Post> findRecentPostsByCollegeSorted(Long collegeId, LocalDateTime oneWeekAgo, Pageable pageable);

    @Query("SELECT p FROM Post p " +
            "LEFT JOIN FETCH p.author " +
            "LEFT JOIN FETCH p.taggedColleges " +
            "WHERE :collegeId IS NULL OR EXISTS (" +
            "  SELECT c FROM p.taggedColleges c WHERE c.id = :collegeId" +
            ")")
    Page<Post> findByTaggedCollegeId(@Param("collegeId") Long collegeId, Pageable pageable);

    @Query("SELECT p FROM Post p " +
            "LEFT JOIN FETCH p.author " +
            "LEFT JOIN FETCH p.taggedColleges " +
            "WHERE p.author.id = :authorId")
    Page<Post> findByAuthorId(@Param("authorId") Long authorId, Pageable pageable);

    @Query("SELECT p FROM Post p " +
            "LEFT JOIN FETCH p.author " +
            "LEFT JOIN FETCH p.taggedColleges")
    Page<Post> findAll(Pageable pageable);


}
