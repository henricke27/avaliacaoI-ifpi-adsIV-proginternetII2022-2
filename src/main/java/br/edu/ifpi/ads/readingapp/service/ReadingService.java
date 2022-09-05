package br.edu.ifpi.ads.readingapp.service;

import br.edu.ifpi.ads.readingapp.domain.Annotation;
import br.edu.ifpi.ads.readingapp.domain.Book;
import br.edu.ifpi.ads.readingapp.domain.User;
import br.edu.ifpi.ads.readingapp.dto.ReadingDto;
import br.edu.ifpi.ads.readingapp.dto.ReadingForm;
import br.edu.ifpi.ads.readingapp.dto.ReadingPageForm;
import br.edu.ifpi.ads.readingapp.dto.ReadingStatusForm;
import br.edu.ifpi.ads.readingapp.repository.ReadingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReadingService {

    private final UserService userService;
    private final ReadingRepository readingRepository;

    public ReadingDto add(ReadingForm readingForm, HttpServletRequest request) {
        User userBySubject = userService.getUserBySubject(request);

        Book book = Book.builder()
                .holder(userBySubject)
                .title(readingForm.getTitle())
                .annotations(new ArrayList<>())
                .page(0)
                .concluded(false)
                .stopped(false)
                .build();

        Book bookSaved = readingRepository.save(book);

        return ReadingDto.builder()
                .holder(userBySubject.getName())
                .title(book.getTitle())
                .annotations(new ArrayList<>())
                .likes(new ArrayList<>())
                .page(0)
                .concluded(false)
                .stopped(false)
                .build();
    }

    public List<ReadingDto> listAll(HttpServletRequest request) {
        User userBySubject = userService.getUserBySubject(request);
        List<Book> books = readingRepository.findByHolder(userBySubject);

        return books.stream().map(book -> {
            return ReadingDto.builder()
                    .title(book.getTitle())
                    .likes(book.getUserLikes().stream()
                            .map(ul -> ul.getUsers().getName())
                            .collect(Collectors.toList()))
                    .page(book.getPage())
                    .holder(book.getHolder().getName())
                    .annotations(book.getAnnotations().stream()
                            .map(Annotation::getAnnotation)
                            .collect(Collectors.toList()))
                    .concluded(book.getConcluded())
                    .stopped(book.getStopped())
                    .build();

        }).collect(Collectors.toList());
    }

    public void removeById(Long id, HttpServletRequest request) {
        Book bookFound = getBookById(id, request);

        readingRepository.delete(bookFound);
    }

    public void changePage(ReadingPageForm readingPageForm, HttpServletRequest request) {
        Book bookFound = getBookById(readingPageForm.getId(), request);

        bookFound.setPage(readingPageForm.getPage());
        readingRepository.save(bookFound);
    }

    public void changeStatus(ReadingStatusForm readingStatusForm, HttpServletRequest request) {
        Book bookFound = getBookById(readingStatusForm.getId(), request);

        bookFound.setConcluded(readingStatusForm.getConcluded());
        bookFound.setStopped(readingStatusForm.getStopped());

        readingRepository.save(bookFound);
    }

    public Book getBookById(Long id, HttpServletRequest request) {
        User subject = userService.getUserBySubject(request);
        List<Book> myBooks = subject.getBooks();

        Book bookFound = myBooks.stream()
                .filter(book -> book.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Leitura não encontrada!"));

        return bookFound;
    }
}
