package com.example.julink.bulk.entity;

import com.example.julink.entryrelated.entity.Users;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Comment {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Users commenter;

    @ManyToOne
    private Post post;

    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime editedAt;
}
