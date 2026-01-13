package com.example.crud.service.iml;

import com.example.crud.entity.Book;
import com.example.crud.entity.Shelf;
import com.example.crud.repository.BookRepository;
import com.example.crud.repository.ShelfRepository;
import com.example.crud.service.ShelfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShelfServiceImpl implements ShelfService {

    private ShelfRepository shelfRepository;
    private BookRepository bookRepository;

    @Autowired
    public ShelfServiceImpl(ShelfRepository shelfRepository, BookRepository bookRepository) {
        this.shelfRepository = shelfRepository;
        this.bookRepository = bookRepository;
    }

    public ShelfServiceImpl() {}

    @Override
    public Shelf createShelf(Shelf shelf) {
        return shelfRepository.save(shelf);
    }

    @Override
    public List<Shelf> readShelf() {
        List<Shelf> shelves = shelfRepository.findAll();

        return shelves;
    }

    @Override
    public Shelf updateShelf(Integer id, Shelf shelf) {
        Shelf existingShelf = shelfRepository.findById(id)
                .orElse(null);

        if (existingShelf != null) {

            existingShelf.setName(shelf.getName());
            existingShelf.setDescription(shelf.getDescription());

            return shelfRepository.save(existingShelf);
        }

        System.out.println("Полка не найдена");
        return null;
    }

    @Override
    public String deleteShelf(Integer id) {
        if (!shelfRepository.existsById(id)) {
            return "Полка не найдена";
        }

        String name  = shelfRepository.findById(id).get().getName();
        shelfRepository.deleteById(id);

        return "Полка " + name + " удалена";
    }

    @Override
    public List<Book> getBooksForShelf(Integer id) {
        Shelf existingShelf = shelfRepository.findById(id)
                .orElse(null);

        if (existingShelf != null) {
            return existingShelf.getBooks();
        }

        System.out.println("Книга не найдена");
        return null;
    }

    @Override
    public String removeBookFromShelfByBookId(Integer bookId) {

        Book book = bookRepository.findById(bookId)
                .orElse(null);

        if (book == null) {
            return "Книга не найдена";
        }

        if (book.getShelf() == null) {
            return "Книга " + book.getTitle() + " не находится ни на одной полке";
        }

        Shelf shelf = book.getShelf();
        String shelfName = shelf.getName();
        String bookName = book.getTitle();

        book.setShelf(null);
        bookRepository.save(book);

        return "Книга '" + bookName + "' удалена с полки '" + shelfName + "'";
    }

    @Override
    public String addBookToShelf(Integer shelfId, Integer bookId) {
        Shelf shelf = shelfRepository.findById(shelfId)
                .orElse(null);

        if (shelf == null) {
            return "Полка не найдена";
        }

        Book book = bookRepository.findById(bookId)
                .orElse(null);

        if (book == null) {
            return "Книга не найдена";
        }

        if (book.getShelf() != null) {
            return "Книга " + book.getTitle() + " уже находится на полке " + book.getShelf().getName();
        }

        book.setShelf(shelf);
        bookRepository.save(book);

        return "Книга '" + book.getTitle() + "' добавлена на полку '" + shelf.getName() + "'";
    }
}
