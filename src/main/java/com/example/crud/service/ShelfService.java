package com.example.crud.service;

import com.example.crud.entity.Book;
import com.example.crud.entity.Shelf;

import java.util.List;

public interface ShelfService {

    Shelf createShelf(Shelf shelf);

    String readShelf();

    Shelf updateShelf(Integer id, Shelf shelf);

    String deleteShelf(Integer id);

    List<Book> getBooksForShelf(Integer id);

    String removeBookFromShelfByBookId(Integer bookId);

    String addBookToShelf(Integer shelfId, Integer bookId);
}
