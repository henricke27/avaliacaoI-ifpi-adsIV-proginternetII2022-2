package br.edu.ifpi.ads.readingapp.controller;

import br.edu.ifpi.ads.readingapp.dto.AnnotationAddForm;
import br.edu.ifpi.ads.readingapp.dto.AnnotationDto;
import br.edu.ifpi.ads.readingapp.dto.AnnotationRemoveForm;
import br.edu.ifpi.ads.readingapp.dto.ReadingDto;
import br.edu.ifpi.ads.readingapp.service.AnnotationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/reading/annotation")
@RequiredArgsConstructor
public class AnnotationController {

    private final AnnotationService annotationService;

    @PostMapping
    public ResponseEntity<ReadingDto> add(@RequestBody AnnotationAddForm annotation, HttpServletRequest request){
        return new ResponseEntity<>(annotationService.add(annotation, request), HttpStatus.CREATED);
    }

    @DeleteMapping
    public ResponseEntity<Void> removeById(@RequestBody AnnotationRemoveForm annotation, HttpServletRequest request){
        annotationService.removeById(annotation, request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/book/{id}")
    public ResponseEntity<List<AnnotationDto>> listAll(@PathVariable Long id, HttpServletRequest request){
        return new ResponseEntity<>(annotationService.listAll(id, request), HttpStatus.OK);
    }
}
