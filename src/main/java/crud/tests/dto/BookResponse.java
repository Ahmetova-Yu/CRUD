package crud.tests.dto;

import lombok.Data;

@Data
public class BookResponse {
    private Integer id;
    private String title;
    private String author;
    private Integer year;
    private ShelfSimpleResponse shelf;

    @Data
    public static class ShelfSimpleResponse {
        private Integer id;
        private String name;
        private String description;
    }
}