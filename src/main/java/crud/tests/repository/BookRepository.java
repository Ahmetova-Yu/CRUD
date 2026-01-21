package crud.tests.repository;

import crud.tests.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {

    @Query("SELECT b FROM Book b ORDER BY LOWER(b.title) ASC")
    List<Book> findAllSortedByTitleAsc();

    @Query("SELECT b FROM Book b ORDER BY LOWER(b.title) DESC")
    List<Book> findAllSortedByTitleDesc();

    @Query("SELECT b FROM Book b ORDER BY LOWER(b.author) ASC")
    List<Book> findAllSortedByAuthorAsc();

    @Query("SELECT b FROM Book b ORDER BY LOWER(b.author) DESC")
    List<Book> findAllSortedByAuthorDesc();

    @Query("SELECT b FROM Book b ORDER BY b.year ASC")
    List<Book> findAllSortedByYearAsc();

    @Query("SELECT b FROM Book b ORDER BY b.year DESC")
    List<Book> findAllSortedByYearDesc();
}