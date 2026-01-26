package com.example.crud.controller;

import com.example.crud.dto.BookWithShelfDTO;
import com.example.crud.entity.Book;
import com.example.crud.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
import java.util.List;

@RestController
@RequestMapping("/book")
public class BookController {

    @Autowired
    private BookService serviceBook;

    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        Book createdBook = serviceBook.createBook(book);
        return new ResponseEntity<>(createdBook, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<String> readBook() {
        String books = serviceBook.readBook();
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Integer id, @RequestBody Book book) {
        Book updatedBook = serviceBook.updateBook(id, book);
        if (updatedBook == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(updatedBook, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Integer id) {
        try {
            serviceBook.deleteBook(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        if (!isValidSortField(sortBy)) {
            sortBy = "title";
        }

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Book> booksPage = serviceBook.getAllBooks(pageable);

        List<Book> books = booksPage.getContent();
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/{id}/with-shelf")
    public ResponseEntity<BookWithShelfDTO> getBookWithShelf(@PathVariable Integer id) {
        try {
            BookWithShelfDTO dto = serviceBook.getBookWithShelf(id);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Integer id) {
        try {
            Book book = serviceBook.getBookById(id);
            return new ResponseEntity<>(book, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Book>> searchBooks(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (keyword == null || keyword.trim().isEmpty()) {
            Pageable pageable = PageRequest.of(page, size);
            Page<Book> booksPage = serviceBook.getAllBooks(pageable);
            return new ResponseEntity<>(booksPage.getContent(), HttpStatus.OK);
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Book> booksPage = serviceBook.searchBooks(keyword, pageable);

        List<Book> books = booksPage.getContent();
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/author")
    public ResponseEntity<List<Book>> findByAuthor(
            @RequestParam String author,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Book> booksPage = serviceBook.findByAuthor(author, pageable);

        List<Book> books = booksPage.getContent();
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/year")
    public ResponseEntity<List<Book>> findByYear(
            @RequestParam Integer year,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Book> booksPage = serviceBook.findByYear(year, pageable);

        List<Book> books = booksPage.getContent();
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/title-author")
    public ResponseEntity<List<Book>> findByTitleAndAuthor(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (title == null && author == null) {
            Pageable pageable = PageRequest.of(page, size);
            Page<Book> booksPage = serviceBook.getAllBooks(pageable);
            return new ResponseEntity<>(booksPage.getContent(), HttpStatus.OK);
        }

        if (title == null) title = "";
        if (author == null) author = "";

        Pageable pageable = PageRequest.of(page, size);
        Page<Book> booksPage = serviceBook.findByTitleAndAuthor(title, author, pageable);

        List<Book> books = booksPage.getContent();
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/sorted/title/asc")
    public ResponseEntity<List<Book>> findAllSortedByTitleAsc() {
        List<Book> books = serviceBook.findAllSortedByTitleAsc();
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/sorted/title/desc")
    public ResponseEntity<List<Book>> findAllSortedByTitleDesc() {
        List<Book> books = serviceBook.findAllSortedByTitleDesc();
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/sorted/author/asc")
    public ResponseEntity<List<Book>> findAllSortedByAuthorAsc() {
        List<Book> books = serviceBook.findAllSortedByAuthorAsc();
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/sorted/author/desc")
    public ResponseEntity<List<Book>> findAllSortedByAuthorDesc() {
        List<Book> books = serviceBook.findAllSortedByAuthorDesc();
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/sorted/year/asc")
    public ResponseEntity<List<Book>> findAllSortedByYearAsc() {
        List<Book> books = serviceBook.findAllSortedByYearAsc();
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/sorted/year/desc")
    public ResponseEntity<List<Book>> findAllSortedByYearDesc() {
        List<Book> books = serviceBook.findAllSortedByYearDesc();
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    private boolean isValidSortField(String field) {
        return field.equals("id") ||
                field.equals("title") ||
                field.equals("author") ||
                field.equals("year");
    }
}