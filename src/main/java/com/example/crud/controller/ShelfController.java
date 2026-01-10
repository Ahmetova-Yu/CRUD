package com.example.crud.controller;

import com.example.crud.entity.Book;
import com.example.crud.entity.Shelf;
import com.example.crud.service.ShelfService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shelf")
@RequiredArgsConstructor
public class ShelfController {

    private ShelfService serviceShelf;

    @PostMapping
    ResponseEntity<Shelf> createShelf(@RequestBody Shelf shelf) {
        try {
            Shelf createdShelf = serviceShelf.createShelf(shelf);
            return new ResponseEntity<>(createdShelf, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    ResponseEntity<String> readShelf() {
        try {
            String shelf = serviceShelf.readShelf();
            return new ResponseEntity<>(shelf, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    ResponseEntity<Shelf> updateShelf(@PathVariable Integer id, @RequestBody Shelf shelf) {
        try {
            Shelf updatedShelf = serviceShelf.updateShelf(id, shelf);
            return new ResponseEntity<>(updatedShelf, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{shelfId}")
    ResponseEntity<String> deleteShelf(@PathVariable Integer shelfId) {
        try {
            String serviceDelete = serviceShelf.deleteShelf(shelfId);
            return new ResponseEntity<>(serviceDelete, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/books")
    public ResponseEntity<List<Book>> getBooks(@PathVariable Integer id) {
        List<Book> books = serviceShelf.getBooksForShelf(id);

        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @DeleteMapping("/books/{bookId}")
    public ResponseEntity<String> removeBookFromShelf(@PathVariable Integer bookId) {
        String result = serviceShelf.removeBookFromShelfByBookId(bookId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/{shelfId}/books/{bookId}")
    public ResponseEntity<String> addBookToShelf(
            @PathVariable Integer shelfId,
            @PathVariable Integer bookId) {
        String result = serviceShelf.addBookToShelf(shelfId, bookId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


}
