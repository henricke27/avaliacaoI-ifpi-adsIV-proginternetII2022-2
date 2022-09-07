package br.edu.ifpi.ads.readingapp.service;

import br.edu.ifpi.ads.readingapp.domain.Annotation;
import br.edu.ifpi.ads.readingapp.domain.Book;
import br.edu.ifpi.ads.readingapp.domain.User;
import br.edu.ifpi.ads.readingapp.domain.UserLikes;
import br.edu.ifpi.ads.readingapp.dto.ReadingDto;
import br.edu.ifpi.ads.readingapp.dto.ReadingForm;
import br.edu.ifpi.ads.readingapp.dto.ReadingPageForm;
import br.edu.ifpi.ads.readingapp.dto.ReadingStatusForm;
import br.edu.ifpi.ads.readingapp.repository.ReadingRepository;
import br.edu.ifpi.ads.readingapp.repository.UserLikesRepository;
import br.edu.ifpi.ads.readingapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class ReadingService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final UserLikesRepository userLikesRepository;
    private final ReadingRepository readingRepository;

    public ReadingDto add(ReadingForm readingForm, HttpServletRequest request) {
        User userBySubject = userService.getUserBySubject(request);
        log.info(userBySubject);

        Book book = Book.builder()
                .holder(userBySubject)
                .title(readingForm.getTitle())
                .annotations(new ArrayList<>())
                .page(1)
                .concluded(false)
                .stopped(false)
                .build();

        Book bookSaved = readingRepository.save(book);

        return ReadingDto.builder()
                .id(bookSaved.getId())
                .holder(userBySubject.getName())
                .title(bookSaved.getTitle())
                .annotations(new ArrayList<>())
                .likes(new ArrayList<>())
                .page(bookSaved.getPage())
                .concluded(false)
                .stopped(false)
                .build();
    }

    public List<ReadingDto> listAll(HttpServletRequest request) {
        User userBySubject = userService.getUserBySubject(request);
        List<Book> books = readingRepository.findByHolder(userBySubject);

        return books.stream().map(book -> ReadingDto.builder()
                .id(book.getId())
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
                .build()).collect(Collectors.toList());
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

        return myBooks.stream()
                .filter(book -> book.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Leitura não encontrada!"));

    }

    public Page<ReadingDto> findAll(Pageable pageable) {
        Page<Book> bookPage = readingRepository.findAll(pageable);

        return bookPage.map(book -> ReadingDto.builder()
                .id(book.getId())
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
                .build());
    }

    public void like(Long id, HttpServletRequest request) {
        User userBySubject = userService.getUserBySubject(request);

        Book bookFound = readingRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Leitura não encontrada!"));

        UserLikes userLikes = UserLikes.builder()
                .book(bookFound)
                .users(userBySubject)
                .build();

        bookFound.getUserLikes().add(userLikes);
        userLikesRepository.save(userLikes);
    }
}
