package br.edu.ifpi.ads.readingapp.service;

import br.edu.ifpi.ads.readingapp.domain.Annotation;
import br.edu.ifpi.ads.readingapp.domain.Book;
import br.edu.ifpi.ads.readingapp.dto.AnnotationAddForm;
import br.edu.ifpi.ads.readingapp.dto.AnnotationDto;
import br.edu.ifpi.ads.readingapp.dto.AnnotationRemoveForm;
import br.edu.ifpi.ads.readingapp.dto.ReadingDto;
import br.edu.ifpi.ads.readingapp.repository.AnnotationRepository;
import br.edu.ifpi.ads.readingapp.repository.ReadingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class AnnotationService {

    private final ReadingService readingService;
    private final AnnotationRepository annotationRepository;

    public ReadingDto add(AnnotationAddForm annotation, HttpServletRequest request) {
        Book bookFound = readingService.getBookById(annotation.getBookId(), request);

        Annotation newAnnotation = Annotation.builder()
                .book(bookFound)
                .annotation(annotation.getAnnotation())
                .build();

        annotationRepository.save(newAnnotation);
        bookFound.getAnnotations().add(newAnnotation);

        Book book = readingService.save(bookFound);

        return ReadingDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .likesNumber(book.getUserLikes().size())
                .likes(book.getUserLikes().stream()
                        .map(ul -> ul.getUserLike().getName())
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
        bookFound.getAnnotations().remove(annotationFound);

        readingService.save(bookFound);
        annotationRepository.deleteById(annotationFound.getId());
    }

    public List<AnnotationDto> listAll(Long id, HttpServletRequest request) {
        Book bookFound = readingService.getBookById(id, request);
        List<AnnotationDto> annotations = new ArrayList<>();

        bookFound.getAnnotations()
                .forEach(annotation -> annotations.add(AnnotationDto.builder()
                        .id(annotation.getId())
                        .annotation(annotation.getAnnotation())
                        .build()));

        log.info(bookFound);
        return annotations;
    }

    public Annotation getAnnotationById(Long id, Book book) {
        return book.getAnnotations().stream()
                .filter(annotation -> annotation.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Anotação não encontrada!"));
    }
}
