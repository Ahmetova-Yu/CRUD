package com.example.crud.service.iml;

import com.example.crud.entity.Book;
import com.example.crud.entity.Shelf;
import com.example.crud.exception.BookNotFoundException;
import com.example.crud.exception.InvalidRequestException;
import com.example.crud.exception.ShelfNotFoundException;
import com.example.crud.repository.BookRepository;
import com.example.crud.repository.ShelfRepository;
import com.example.crud.service.ShelfService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class ShelfServiceImpl implements ShelfService {

    private final ShelfRepository shelfRepository;
    private final BookRepository bookRepository;

    public ShelfServiceImpl(ShelfRepository shelfRepository, BookRepository bookRepository) {
        this.shelfRepository = shelfRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    @Transactional
    public Shelf createShelf(Shelf shelf) {
        validateShelfForCreation(shelf);
        log.info("Создание новой полки: {}", shelf.getName());
        return shelfRepository.save(shelf);
    }

    @Override
    public List<Shelf> readShelf() {
        List<Shelf> shelves = shelfRepository.findAll();

        return shelves;
    }

    @Override
    @Transactional
    public Shelf updateShelf(Integer id, Shelf shelf) {
        validateShelfForUpdate(shelf);

        Shelf existingShelf = shelfRepository.findById(id)
                .orElseThrow(() -> new ShelfNotFoundException("Полка с ID " + id + " не найдена"));

        log.info("Обновление полки ID: {}, старое название: '{}', новое название: '{}'",
                id, existingShelf.getName(), shelf.getName());

        existingShelf.setName(shelf.getName());
        existingShelf.setDescription(shelf.getDescription());

        return shelfRepository.save(existingShelf);
    }

    @Override
    @Transactional
    public String deleteShelf(Integer id) {
        Shelf shelf = shelfRepository.findById(id)
                .orElseThrow(() -> new ShelfNotFoundException("Полка с ID " + id + " не найдена"));

        if (!shelf.getBooks().isEmpty()) {
            throw new InvalidRequestException(
                    "Нельзя удалить полку '" + shelf.getName() + "', так как на ней находятся книги. " +
                            "Сначала переместите или удалите книги."
            );
        }

        String shelfName = shelf.getName();
        shelfRepository.delete(shelf);
        log.info("Полка '{}' (ID: {}) удалена", shelfName, id);
        return shelfName;
    }

    public Shelf getShelfById(Integer id) {
        return shelfRepository.findById(id)
                .orElseThrow(() -> new ShelfNotFoundException("Полка с ID " + id + " не найдена"));
    }

    @Override
    public List<Book> getBooksForShelf(Integer id) {
        Shelf shelf = getShelfById(id);
        List<Book> books = shelf.getBooks();

        if (books.isEmpty()) {
            log.info("На полке '{}' нет книг", shelf.getName());
        } else {
            log.debug("На полке '{}' найдено {} книг", shelf.getName(), books.size());
        }

        return books;
    }

    @Override
    public ResponseEntity removeBookFromShelfByBookId(Integer bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Книга с ID " + bookId + " не найдена"));

        if (book.getShelf() == null) {
            throw new InvalidRequestException("Книга '" + book.getTitle() + "' не находится на полке");
        }

        Shelf shelf = book.getShelf();
        String shelfName = shelf.getName();
        String bookTitle = book.getTitle();

        book.setShelf(null);
        bookRepository.save(book);

        log.info("Книга '{}' удалена с полки '{}'", bookTitle, shelfName);
        return null;
    }

    @Override
    @Transactional
    public void addBookToShelf(Integer shelfId, Integer bookId) {
        Shelf shelf = shelfRepository.findById(shelfId)
                .orElseThrow(() -> new ShelfNotFoundException("Полка с ID " + shelfId + " не найдена"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Книга с ID " + bookId + " не найдена"));

        if (book.getShelf() != null) {
            throw new InvalidRequestException(
                    "Книга '" + book.getTitle() + "' уже находится на полке '" +
                            book.getShelf().getName() + "'. Сначала удалите книгу с текущей полки."
            );
        }

        book.setShelf(shelf);
        bookRepository.save(book);

        log.info("Книга '{}' добавлена на полку '{}'", book.getTitle(), shelf.getName());
    }

    @Override
    @Transactional
    public void clearShelf(Integer shelfId) {
        Shelf shelf = getShelfById(shelfId);
        List<Book> books = shelf.getBooks();

        if (books.isEmpty()) {
            log.info("Полка '{}' уже пуста", shelf.getName());
            return;
        }

        for (Book book : books) {
            book.setShelf(null);
        }

        bookRepository.saveAll(books);
        log.info("С полки '{}' удалено {} книг", shelf.getName(), books.size());
    }

    private void validateShelfForCreation(Shelf shelf) {
        if (shelf == null) {
            throw new InvalidRequestException("Данные полки не могут быть null");
        }

        if (shelf.getName() == null || shelf.getName().trim().isEmpty()) {
            throw new InvalidRequestException("Название полки обязательно");
        }

        if (shelf.getName().length() > 100) {
            throw new InvalidRequestException("Название полки не может превышать 100 символов");
        }

        if (shelf.getDescription() != null && shelf.getDescription().length() > 500) {
            throw new InvalidRequestException("Описание полки не может превышать 500 символов");
        }
    }

    private void validateShelfForUpdate(Shelf shelf) {
        if (shelf == null) {
            throw new InvalidRequestException("Данные для обновления не могут быть null");
        }

        if (shelf.getName() != null) {
            if (shelf.getName().trim().isEmpty()) {
                throw new InvalidRequestException("Название полки не может быть пустым");
            }

            if (shelf.getName().length() > 100) {
                throw new InvalidRequestException("Название полки не может превышать 100 символов");
            }
        }

        if (shelf.getDescription() != null && shelf.getDescription().length() > 500) {
            throw new InvalidRequestException("Описание полки не может превышать 500 символов");
        }
    }
}