package com.example.crud.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String title;
    private String author;
    private Integer year;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "shelf_id", nullable = true)
    private Shelf shelf;
}
