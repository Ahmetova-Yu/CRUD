package com.example.crud.dto;

import lombok.Data;

@Data
public class BookRequest {
    private String title;
    private String author;
    private Integer year;
    private Integer shelfId;
}