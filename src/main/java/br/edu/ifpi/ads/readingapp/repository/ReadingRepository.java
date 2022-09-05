package br.edu.ifpi.ads.readingapp.repository;

import br.edu.ifpi.ads.readingapp.domain.Book;
import br.edu.ifpi.ads.readingapp.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ReadingRepository extends JpaRepository<Book, Long> {
    List<Book> findByHolder(User userBySubject);
}
