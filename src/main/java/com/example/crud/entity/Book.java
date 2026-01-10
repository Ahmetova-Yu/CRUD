package com.example.crud.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
//@AllArgsConstructor
//@NoArgsConstructor
//@Getter
//@Setter
//@ToString
//@EqualsAndHashCode
@Data
//@RequiredArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String title;
    private String author;
    private Integer year;

    @ManyToOne
    @JoinColumn(name = "shelf_id")
    private Shelf shelf;
}
