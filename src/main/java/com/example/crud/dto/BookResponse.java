package com.example.crud.dto;

import lombok.Data;

@Data
public class BookResponse {
    private Integer id;
    private String title;
    private String author;
    private Integer year;
    private ShelfSimpleResponse shelf; // Только нужные поля

    @Data
    public static class ShelfSimpleResponse {
        private Integer id;
        private String name;
        private String description;
    }
}