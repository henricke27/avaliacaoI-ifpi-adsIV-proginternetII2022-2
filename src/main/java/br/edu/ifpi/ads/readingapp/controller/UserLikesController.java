package br.edu.ifpi.ads.readingapp.controller;

import br.edu.ifpi.ads.readingapp.service.UserLikesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reading")
public class UserLikesController {

    private final UserLikesService userLikesService;

    @PostMapping("/like/book/{id}")
    public ResponseEntity<Void> like(@PathVariable Long id, HttpServletRequest request){
        userLikesService.like(id, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
