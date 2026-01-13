package com.example.crud.service;

import com.example.crud.dto.BookRequest;
import com.example.crud.dto.BookWithShelfDTO;
import com.example.crud.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

public interface BookService {

    Book createBook(Book book);

    String readBook();

    Book updateBook(Integer id, Book book);

    String deleteBook(Integer id);

    Page<Book> getAllBooks(Pageable pageable);

    Page<Book> searchBooks(String keyword, Pageable pageable);

    Page<Book> findByAuthor(String author, Pageable pageable);

    Page<Book> findByYear(Integer year, Pageable pageable);

    Page<Book> findByTitleAndAuthor(String title, String author, Pageable pageable);

    List<Book> findAllSortedByTitle();

    List<Book> findAllSortedByAuthor();

    List<Book> findAllSortedByYear();

    Book getBookById(Integer id); // Новый метод
    BookWithShelfDTO getBookWithShelf(Integer id);
}
