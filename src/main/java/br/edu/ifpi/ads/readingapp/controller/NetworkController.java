package br.edu.ifpi.ads.readingapp.controller;

import br.edu.ifpi.ads.readingapp.dto.ReadingDto;
import br.edu.ifpi.ads.readingapp.service.ReadingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class NetworkController {

    private final ReadingService readingService;

    @GetMapping("/timeline")
    public ResponseEntity<Page<ReadingDto>> findAll(Pageable pageable){
        return new ResponseEntity<>(readingService.findAll(pageable), HttpStatus.OK);
    }

    @PostMapping("/like/book/{id}")
    public ResponseEntity<Void> like(@PathVariable Long id, HttpServletRequest request){
        readingService.like(id, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
