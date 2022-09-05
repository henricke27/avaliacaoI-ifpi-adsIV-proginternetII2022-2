package br.edu.ifpi.ads.readingapp.controller;

import br.edu.ifpi.ads.readingapp.domain.Annotation;
import br.edu.ifpi.ads.readingapp.dto.AnnotationAddForm;
import br.edu.ifpi.ads.readingapp.dto.AnnotationRemoveForm;
import br.edu.ifpi.ads.readingapp.dto.ReadingDto;
import br.edu.ifpi.ads.readingapp.service.AnnotationService;
import br.edu.ifpi.ads.readingapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/annotation")
@RequiredArgsConstructor
public class AnnotationController {

    private final AnnotationService annotationService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ReadingDto> add(@RequestBody AnnotationAddForm annotation, HttpServletRequest request){
        return new ResponseEntity<>(annotationService.add(annotation, request), HttpStatus.CREATED);
    }

    @DeleteMapping
    public ResponseEntity<ReadingDto> removeById(@RequestBody AnnotationRemoveForm annotation, HttpServletRequest request){
        annotationService.removeById(annotation, request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/book/{id}")
    public ResponseEntity<List<Annotation>> listAll(@PathVariable Long id, HttpServletRequest request){
        annotationService.listAll(id, request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
