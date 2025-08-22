package com.example.julink.bulk.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "colleges")
@Getter
@Setter
@Builder
@AllArgsConstructor
public class College {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;
    public College(String name) {
        this.name = name;
    }

    public College() {

    }
}
