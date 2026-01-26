package com.example.crud.controller;

import com.example.crud.entity.Book;
import com.example.crud.entity.Shelf;
import com.example.crud.service.ShelfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shelf")
public class ShelfController {

    @Autowired
    private ShelfService serviceShelf;

    @PostMapping
    public ResponseEntity<Shelf> createShelf(@RequestBody Shelf shelf) {
        Shelf createdShelf = serviceShelf.createShelf(shelf);
        return new ResponseEntity<>(createdShelf, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Shelf>> readShelf() {
        List<Shelf> shelves = serviceShelf.readShelf();
        return new ResponseEntity<>(shelves, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Shelf> updateShelf(@PathVariable Integer id, @RequestBody Shelf shelf) {
        Shelf updatedShelf = serviceShelf.updateShelf(id, shelf);
        if (updatedShelf == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(updatedShelf, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteShelf(@PathVariable Integer id) {
        String result = serviceShelf.deleteShelf(id);
        if (result.equals("Полка не найдена")) {
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/{id}/books")
    public ResponseEntity<List<Book>> getBooksForShelf(@PathVariable Integer id) {
        List<Book> books = serviceShelf.getBooksForShelf(id);
        if (books == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @DeleteMapping("/books/{bookId}")
    public ResponseEntity<String> removeBookFromShelf(@PathVariable Integer bookId) {
        ResponseEntity response = serviceShelf.removeBookFromShelfByBookId(bookId);
        return response;
    }

    @PostMapping("/{shelfId}/books/{bookId}")
    public ResponseEntity<Void> addBookToShelf(
            @PathVariable Integer shelfId,
            @PathVariable Integer bookId) {
        serviceShelf.addBookToShelf(shelfId, bookId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}/clear")
    public ResponseEntity<Void> clearShelf(@PathVariable Integer id) {
        serviceShelf.clearShelf(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}