package crud.tests.service;

import crud.tests.entity.Book;
import crud.tests.entity.Shelf;

import java.util.List;

public interface ShelfService {

    Shelf createShelf(Shelf shelf);

    List<Shelf> readShelf();

    Shelf updateShelf(Integer id, Shelf shelf);

    String deleteShelf(Integer id);

    List<Book> getBooksForShelf(Integer id);

    String removeBookFromShelfByBookId(Integer bookId);

    String addBookToShelf(Integer shelfId, Integer bookId);
}
