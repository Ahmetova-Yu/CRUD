package com.example.crud.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
public class Shelf {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int shelf_id;

    private String name;
    private String description;

    @OneToMany(mappedBy = "shelf")
    List<Book> books;
}
