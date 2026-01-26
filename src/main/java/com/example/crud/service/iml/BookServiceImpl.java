package com.example.crud.service.iml;

import com.example.crud.dto.BookWithShelfDTO;
import com.example.crud.entity.Book;
import com.example.crud.entity.Shelf;
import com.example.crud.exception.BookNotFoundException;
import com.example.crud.exception.ShelfNotFoundException;
import com.example.crud.exception.InvalidRequestException;
import com.example.crud.repository.BookRepository;
import com.example.crud.repository.ShelfRepository;
import com.example.crud.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final ShelfRepository shelfRepository;

    public BookServiceImpl(BookRepository bookRepository, ShelfRepository shelfRepository) {
        this.bookRepository = bookRepository;
        this.shelfRepository = shelfRepository;
    }

    @Override
    @Transactional
    public Book createBook(Book book) {
        validateBookForCreation(book);

        if (book.getShelf() != null) {
            Integer shelfId = book.getShelf().getId();

            Shelf shelf = shelfRepository.findById(shelfId)
                    .orElseThrow(() -> new ShelfNotFoundException("Полка с ID " + shelfId + " не найдена"));

            book.setShelf(shelf);
            log.info("Книга '{}' привязана к полке ID: {}", book.getTitle(), shelfId);
        } else {
            book.setShelf(null);
        }

        return bookRepository.save(book);
    }

    @Override
    public Book getBookById(Integer id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Книга с ID " + id + " не найдена"));
    }

    @Override
    public BookWithShelfDTO getBookWithShelf(Integer id) {
        Book book = getBookById(id);
        return convertToBookWithShelfDTO(book);
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

    @Override
    public String readBook() {
        List<Book> books = bookRepository.findAll();

        if (books.isEmpty()) {
            return "Библиотека пуста";
        }

        return books.stream()
                .map(Book::toString)
                .collect(Collectors.joining("\n"));
    }

    @Override
    @Transactional
    public Book updateBook(Integer id, Book book) {
        validateBookForUpdate(book);

        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Книга с ID " + id + " не найдена"));

        existingBook.setTitle(book.getTitle());
        existingBook.setAuthor(book.getAuthor());
        existingBook.setYear(book.getYear());

        if (book.getShelf() != null) {
            Shelf shelf = shelfRepository.findById(book.getShelf().getId())
                    .orElseThrow(() -> new ShelfNotFoundException("Полка с ID " + book.getShelf().getId() + " не найдена"));
            existingBook.setShelf(shelf);
        } else {
            existingBook.setShelf(null);
        }

        return bookRepository.save(existingBook);
    }

    @Override
    @Transactional
    public void deleteBook(Integer id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Книга с ID " + id + " не найдена"));

        String bookTitle = book.getTitle();
        bookRepository.delete(book);
        log.info("Книга '{}' (ID: {}) удалена", bookTitle, id);
    }

    @Override
    public Page<Book> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    @Override
    public Page<Book> searchBooks(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new InvalidRequestException("Ключевое слово для поиска не может быть пустым");
        }

        List<Book> filteredBooks = bookRepository.findAll().stream()
                .filter(book -> book.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                        book.getAuthor().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());

        return createPageFromList(filteredBooks, pageable);
    }

    @Override
    public Page<Book> findByAuthor(String author, Pageable pageable) {
        if (author == null || author.trim().isEmpty()) {
            throw new InvalidRequestException("Имя автора не может быть пустым");
        }

        List<Book> filteredBooks = bookRepository.findAll().stream()
                .filter(book -> book.getAuthor().toLowerCase().contains(author.toLowerCase()))
                .collect(Collectors.toList());

        if (filteredBooks.isEmpty()) {
            log.warn("Книги автора '{}' не найдены", author);
        }

        return createPageFromList(filteredBooks, pageable);
    }

    @Override
    public Page<Book> findByYear(Integer year, Pageable pageable) {
        if (year == null) {
            throw new InvalidRequestException("Год не может быть null");
        }

        if (year < 0 || year > 2100) {
            throw new InvalidRequestException("Год должен быть в диапазоне от 0 до 2100");
        }

        List<Book> filteredBooks = bookRepository.findAll().stream()
                .filter(book -> year.equals(book.getYear()))
                .collect(Collectors.toList());

        if (filteredBooks.isEmpty()) {
            log.info("Книги за {} год не найдены", year);
        }

        return createPageFromList(filteredBooks, pageable);
    }

    @Override
    public Page<Book> findByTitleAndAuthor(String title, String author, Pageable pageable) {
        if ((title == null || title.trim().isEmpty()) && (author == null || author.trim().isEmpty())) {
            throw new InvalidRequestException("Хотя бы один параметр поиска (название или автор) должен быть указан");
        }

        List<Book> filteredBooks = bookRepository.findAll().stream()
                .filter(book -> {
                    boolean matchesTitle = title == null || title.trim().isEmpty() ||
                            book.getTitle().toLowerCase().contains(title.toLowerCase());
                    boolean matchesAuthor = author == null || author.trim().isEmpty() ||
                            book.getAuthor().toLowerCase().contains(author.toLowerCase());
                    return matchesTitle && matchesAuthor;
                })
                .collect(Collectors.toList());

        return createPageFromList(filteredBooks, pageable);
    }

    @Override
    public List<Book> findAllSortedByTitleAsc() {
        return bookRepository.findAllSortedByTitleAsc();
    }

    @Override
    public List<Book> findAllSortedByTitleDesc() {
        return bookRepository.findAllSortedByTitleDesc();
    }

    @Override
    public List<Book> findAllSortedByAuthorAsc() {
        return bookRepository.findAllSortedByAuthorAsc();
    }

    @Override
    public List<Book> findAllSortedByAuthorDesc() {
        return bookRepository.findAllSortedByAuthorDesc();
    }

    @Override
    public List<Book> findAllSortedByYearAsc() {
        return bookRepository.findAllSortedByYearAsc();
    }

    @Override
    public List<Book> findAllSortedByYearDesc() {
        return bookRepository.findAllSortedByYearDesc();
    }

    private Page<Book> createPageFromList(List<Book> list, Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;

        List<Book> pageList;

        if (list.size() < startItem) {
            pageList = new ArrayList<>();
        } else {
            int toIndex = Math.min(startItem + pageSize, list.size());
            pageList = list.subList(startItem, toIndex);
        }

        if (pageable.getSort().isSorted()) {
            pageList = applySorting(pageList, pageable.getSort());
        }

        return new PageImpl<>(pageList, pageable, list.size());
    }

    private List<Book> applySorting(List<Book> books, Sort sort) {
        List<Comparator<Book>> comparators = new ArrayList<>();

        for (Sort.Order order : sort) {
            Comparator<Book> comparator;

            switch (order.getProperty().toLowerCase()) {
                case "title":
                    comparator = Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER);
                    break;
                case "author":
                    comparator = Comparator.comparing(Book::getAuthor, String.CASE_INSENSITIVE_ORDER);
                    break;
                case "year":
                    comparator = Comparator.comparing(Book::getYear);
                    break;
                default:
                    comparator = Comparator.comparing(Book::getId);
                    break;
            }

            if (order.isDescending()) {
                comparator = comparator.reversed();
            }

            comparators.add(comparator);
        }

        if (!comparators.isEmpty()) {
            Comparator<Book> combinedComparator = comparators.get(0);
            for (int i = 1; i < comparators.size(); i++) {
                combinedComparator = combinedComparator.thenComparing(comparators.get(i));
            }

            books.sort(combinedComparator);
        }

        return books;
    }

    private void validateBookForCreation(Book book) {
        if (book == null) {
            throw new InvalidRequestException("Данные книги не могут быть null");
        }

        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new InvalidRequestException("Название книги обязательно");
        }

        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            throw new InvalidRequestException("Автор книги обязателен");
        }

        if (book.getYear() == null) {
            throw new InvalidRequestException("Год издания обязателен");
        }

        if (book.getYear() < 0 || book.getYear() > 2100) {
            throw new InvalidRequestException("Год издания должен быть в диапазоне от 0 до 2100");
        }
    }

    private void validateBookForUpdate(Book book) {
        if (book == null) {
            throw new InvalidRequestException("Данные для обновления не могут быть null");
        }

        if (book.getTitle() != null && book.getTitle().trim().isEmpty()) {
            throw new InvalidRequestException("Название книги не может быть пустым");
        }

        if (book.getAuthor() != null && book.getAuthor().trim().isEmpty()) {
            throw new InvalidRequestException("Автор книги не может быть пустым");
        }

        if (book.getYear() != null && (book.getYear() < 0 || book.getYear() > 2100)) {
            throw new InvalidRequestException("Год издания должен быть в диапазоне от 0 до 2100");
        }
    }
}