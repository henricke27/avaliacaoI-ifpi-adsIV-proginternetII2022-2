package br.edu.ifpi.ads.readingapp.service;

import br.edu.ifpi.ads.readingapp.domain.Annotation;
import br.edu.ifpi.ads.readingapp.domain.Book;
import br.edu.ifpi.ads.readingapp.dto.AnnotationAddForm;
import br.edu.ifpi.ads.readingapp.dto.AnnotationRemoveForm;
import br.edu.ifpi.ads.readingapp.dto.ReadingDto;
import br.edu.ifpi.ads.readingapp.repository.AnnotationRepository;
import br.edu.ifpi.ads.readingapp.repository.ReadingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnnotationService {

    private final ReadingService readingService;
    private final ReadingRepository readingRepository;
    private final AnnotationRepository annotationRepository;
    public ReadingDto add(AnnotationAddForm annotation, HttpServletRequest request) {
        Book bookFound = readingService.getBookById(annotation.getId(), request);

        Annotation newAnnotation = Annotation.builder()
                .book(bookFound)
                .annotation(annotation.getAnnotation())
                .order(annotation.getOrder())
                .build();

        bookFound.getAnnotations().add(newAnnotation);

        Book book = readingRepository.save(bookFound);

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
    }

    public void removeById(AnnotationRemoveForm annotationRemoveForm, HttpServletRequest request) {
        Book bookFound = readingService.getBookById(annotationRemoveForm.getBookId(), request);
        Annotation annotationFound = getAnnotationById(annotationRemoveForm.getAnnotationId(), bookFound);

        annotationRepository.delete(annotationFound);
    }

    public List<Annotation> listAll(Long id, HttpServletRequest request) {
        Book bookFound = readingService.getBookById(id ,request);

        return bookFound.getAnnotations();
    }
    public Annotation getAnnotationById(Long id, Book book) {
        return book.getAnnotations().stream()
                .filter(annotation -> annotation.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Anotação não encontrada!"));
    }
}
