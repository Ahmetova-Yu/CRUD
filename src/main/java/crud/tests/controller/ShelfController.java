package crud.tests.controller;

import crud.tests.entity.Book;
import crud.tests.entity.Shelf;
import crud.tests.service.ShelfService;
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
    ResponseEntity<Shelf> createShelf(@RequestBody Shelf shelf) {
        Shelf createdShelf = serviceShelf.createShelf(shelf);
        return new ResponseEntity<>(createdShelf, HttpStatus.CREATED);
    }

    @GetMapping
    ResponseEntity<List<Shelf>> readShelf() {
        List<Shelf> shelf = serviceShelf.readShelf();
        return new ResponseEntity<>(shelf, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    ResponseEntity<Shelf> updateShelf(@PathVariable Integer id, @RequestBody Shelf shelf) {
        Shelf updatedShelf = serviceShelf.updateShelf(id, shelf);
        return new ResponseEntity<>(updatedShelf, HttpStatus.OK);
    }

    @DeleteMapping("/{shelfId}")
    ResponseEntity<String> deleteShelf(@PathVariable Integer shelfId) {
        String serviceDelete = serviceShelf.deleteShelf(shelfId);
        return new ResponseEntity<>(serviceDelete, HttpStatus.OK);
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
