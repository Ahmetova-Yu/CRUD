package com.example.crud.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "shelf")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "books")
public class Shelf {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "shelf_id")
    private int id;

    private String name;
    private String description;

    @OneToMany(mappedBy = "shelf")
    List<Book> books;
}
