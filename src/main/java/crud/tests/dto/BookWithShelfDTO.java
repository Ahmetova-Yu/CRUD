package crud.tests.dto;

import lombok.Data;

@Data
public class BookWithShelfDTO {
    private Integer id;
    private String title;
    private String author;
    private Integer year;
    private ShelfSimpleDTO shelf;

    @Data
    public static class ShelfSimpleDTO {
        private Integer id;
        private String name;
        private String description;
    }
}