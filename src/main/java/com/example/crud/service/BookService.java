package com.example.crud.service;

import com.example.crud.dto.BookWithShelfDTO;
import com.example.crud.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

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

    List<Book> findAllSortedByTitleAsc();

    List<Book> findAllSortedByTitleDesc();

    List<Book> findAllSortedByAuthorAsc();

    List<Book> findAllSortedByAuthorDesc();

    List<Book> findAllSortedByYearAsc();

    List<Book> findAllSortedByYearDesc();

    Book getBookById(Integer id);

    BookWithShelfDTO getBookWithShelf(Integer id);
}
