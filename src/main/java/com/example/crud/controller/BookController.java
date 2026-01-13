package com.example.crud.controller;

import com.example.crud.dto.BookRequest;
import com.example.crud.dto.BookResponse;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/book")
public class BookController {

    @Autowired
    private BookService serviceBook;

//    @PostMapping
//    public ResponseEntity<Book> createBook(@RequestBody Book book) {
//        Book createdBook = serviceBook.createBook(book);
//        return new ResponseEntity<>(createdBook, HttpStatus.CREATED);
//    }

    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        Book createdBook = serviceBook.createBook(book);
        return new ResponseEntity<>(createdBook, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<String> readBook() {
        String book = serviceBook.readBook();
        return new ResponseEntity<>(book, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Integer id, @RequestBody Book book) {
        Book updatedBook = serviceBook.updateBook(id, book);
        return new ResponseEntity<>(updatedBook, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable Integer id) {
        String deleteBook = serviceBook.deleteBook(id);
        return new ResponseEntity<>(deleteBook, HttpStatus.OK);
    }

//    @GetMapping
//    public ResponseEntity<List<BookResponse>> getAllBooks(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size,
//            @RequestParam(defaultValue = "title") String sortBy,
//            @RequestParam(defaultValue = "asc") String direction) {
//
//        Sort sort = direction.equalsIgnoreCase("desc")
//                ? Sort.by(sortBy).descending()
//                : Sort.by(sortBy).ascending();
//
//        Pageable pageable = PageRequest.of(page, size, sort);
//        Page<Book> booksPage = serviceBook.getAllBooks(pageable);
//
//        List<BookResponse> response = booksPage.getContent().stream()
//                .map(this::convertToResponse)
//                .collect(Collectors.toList());
//
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Book> booksPage = serviceBook.getAllBooks(pageable);

        List<Book> books = booksPage.getContent();
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

//    private BookResponse convertToResponse(Book book) {
//        BookResponse response = new BookResponse();
//        response.setId(book.getId());
//        response.setTitle(book.getTitle());
//        response.setAuthor(book.getAuthor());
//        response.setYear(book.getYear());
//
//        if (book.getShelf() != null) {
//            BookResponse.ShelfSimpleResponse shelf = new BookResponse.ShelfSimpleResponse();
//            shelf.setId(book.getShelf().getId());
//            shelf.setName(book.getShelf().getName());
//            shelf.setDescription(book.getShelf().getDescription());
//            response.setShelf(shelf);
//        }
//
//        return response;
//    }

    @GetMapping("/{id}/with-shelf")
    public ResponseEntity<BookWithShelfDTO> getBookWithShelf(@PathVariable Integer id) {
        Book book = serviceBook.getBookById(id); // Нужно добавить этот метод в сервис
        BookWithShelfDTO dto = convertToBookWithShelfDTO(book);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    private BookWithShelfDTO convertToBookWithShelfDTO(Book book) {
        BookWithShelfDTO dto = new BookWithShelfDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setYear(book.getYear());

        if (book.getShelf() != null) {
            BookWithShelfDTO.ShelfSimpleDTO shelfDTO = new BookWithShelfDTO.ShelfSimpleDTO();
            shelfDTO.setId(book.getShelf().getId());
            shelfDTO.setName(book.getShelf().getName());
            shelfDTO.setDescription(book.getShelf().getDescription());
            dto.setShelf(shelfDTO);
        }

        return dto;
    }

    @GetMapping("/search")
    public ResponseEntity<List<Book>> searchBooks(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

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
            @RequestParam String title,
            @RequestParam String author,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Book> booksPage = serviceBook.findByTitleAndAuthor(title, author, pageable);

        List<Book> books = booksPage.getContent();

        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/sorted/title")
    public ResponseEntity<List<Book>> findAllSortedByTitle() {
        List<Book> books = serviceBook.findAllSortedByTitle();
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/sorted/author")
    public ResponseEntity<List<Book>> findAllSortedByAuthor() {
        List<Book> books = serviceBook.findAllSortedByAuthor();
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/sorted/year")
    public ResponseEntity<List<Book>> findAllSortedByYear() {
        List<Book> books = serviceBook.findAllSortedByYear();
        return new ResponseEntity<>(books, HttpStatus.OK);
    }
}