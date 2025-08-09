package com.example.julink.bulk.entity;

import com.example.julink.entryrelated.entity.Users;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
public class Post {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Users author;

    @Column(nullable = false)
    private String content;

    @ManyToMany
    private Set<College> taggedColleges = new HashSet<>();

    private LocalDateTime createdAt;
    private LocalDateTime editedAt;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Like> likes = new HashSet<>();
}
