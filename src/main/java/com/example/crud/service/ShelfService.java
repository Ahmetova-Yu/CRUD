package com.example.crud.service;

import com.example.crud.entity.Book;
import com.example.crud.entity.Shelf;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ShelfService {

    Shelf createShelf(Shelf shelf);

    List<Shelf> readShelf();

    Shelf updateShelf(Integer id, Shelf shelf);

    String deleteShelf(Integer id);

    List<Book> getBooksForShelf(Integer id);

    ResponseEntity removeBookFromShelfByBookId(Integer bookId);

    void addBookToShelf(Integer shelfId, Integer bookId);

    void clearShelf(Integer id);
}
