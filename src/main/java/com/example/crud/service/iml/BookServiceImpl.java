package com.example.crud.service.iml;

import com.example.crud.dto.BookWithShelfDTO;
import com.example.crud.entity.Book;
import com.example.crud.entity.Shelf;
import com.example.crud.repository.BookRepository;
import com.example.crud.repository.ShelfRepository;
import com.example.crud.service.BookService;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {
    private BookRepository repository;
    private ShelfRepository shelfRepository;

    public BookServiceImpl(BookRepository repository, ShelfRepository shelfRepository) {
        this.repository = repository;
        this.shelfRepository = shelfRepository;
    }

    @Override
    @Transactional
    public Book createBook(Book book) {
        if (book.getShelf() != null) {
            Integer shelfId = book.getShelf().getId();

            Optional<Shelf> shelfOpt = shelfRepository.findById(shelfId);

            if (shelfOpt.isPresent()) {
                book.setShelf(shelfOpt.get());
                System.out.println("Книга привязана к полке ID: " + shelfId);
            } else {
                // НЕ НАЙДЕНО: обнуляем полку
                System.out.println("ВНИМАНИЕ: Полка с ID " + shelfId + " не найдена!");
                book.setShelf(null);
            }
        }

        return repository.save(book);
    }

    @Override
    public Book getBookById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
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
        List<Book> books = repository.findAll();

        return books.stream().map(Book::toString).collect(Collectors.joining("\n"));
    }

    @Override
    public Book updateBook(Integer id, Book book) {
        Book existingBook = repository.findById(id)
                .orElse(null);


        if (existingBook != null) {

            existingBook.setTitle(book.getTitle());
            existingBook.setAuthor(book.getAuthor());
            existingBook.setYear(book.getYear());
            existingBook.setShelf(book.getShelf());

            return repository.save(existingBook);
        }

        System.out.println("Книга не найдена");
        return null;
    }

    @Override
    public String deleteBook(Integer id) {
        if (!repository.existsById(id)) {
            return "Книга не найдена";
        }

        String name  = repository.findById(id).get().getTitle();

        repository.deleteById(id);
        return "Книга " + name + " удалена";
    }

    @Override
    public Page<Book> getAllBooks(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Page<Book> searchBooks(String keyword, Pageable pageable) {
        List<Book> allBooks = repository.findAll();

        List<Book> filteredBooks = allBooks.stream()
                .filter(book ->
                        book.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                                book.getAuthor().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());

        return createPageFromList(filteredBooks, pageable);
    }

    @Override
    public Page<Book> findByAuthor(String author, Pageable pageable) {
        List<Book> allBooks = repository.findAll();

        List<Book> filteredBooks = allBooks.stream()
                .filter(book -> book.getAuthor().toLowerCase().contains(author.toLowerCase()))
                .collect(Collectors.toList());

        return createPageFromList(filteredBooks, pageable);
    }

    @Override
    public Page<Book> findByYear(Integer year, Pageable pageable) {
        List<Book> allBooks = repository.findAll();

        List<Book> filteredBooks = allBooks.stream()
                .filter(book -> book.getYear().equals(year))
                .collect(Collectors.toList());

        return createPageFromList(filteredBooks, pageable);
    }

    @Override
    public Page<Book> findByTitleAndAuthor(String title, String author, Pageable pageable) {
        List<Book> allBooks = repository.findAll();

        List<Book> filteredBooks = allBooks.stream()
                .filter(book ->
                        book.getTitle().toLowerCase().contains(title.toLowerCase()) &&
                                book.getAuthor().toLowerCase().contains(author.toLowerCase()))
                .collect(Collectors.toList());

        return createPageFromList(filteredBooks, pageable);
    }

    @Override
    public List<Book> findAllSortedByTitleAsc() {
        return repository.findAllSortedByTitleAsc();
    }

    @Override
    public List<Book> findAllSortedByTitleDesc() {
        return repository.findAllSortedByTitleDesc();
    }

    @Override
    public List<Book> findAllSortedByAuthorAsc() {
        return repository.findAllSortedByAuthorAsc();
    }

    @Override
    public List<Book> findAllSortedByAuthorDesc() {
        return repository.findAllSortedByAuthorDesc();
    }

    @Override
    public List<Book> findAllSortedByYearAsc() {
        return repository.findAllSortedByYearAsc();
    }

    @Override
    public List<Book> findAllSortedByYearDesc() {
        return repository.findAllSortedByYearDesc();
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
}
