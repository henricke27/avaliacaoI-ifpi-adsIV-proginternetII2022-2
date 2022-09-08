package br.edu.ifpi.ads.readingapp.service;

import br.edu.ifpi.ads.readingapp.domain.Book;
import br.edu.ifpi.ads.readingapp.domain.User;
import br.edu.ifpi.ads.readingapp.domain.UserLikes;
import br.edu.ifpi.ads.readingapp.repository.UserLikesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class UserLikesService {

    private final UserService userService;
    private final ReadingService readingService;
    private final UserLikesRepository userLikesRepository;

    public UserLikes save(UserLikes userLikes){
        return userLikesRepository.save(userLikes);
    }

    public void like(Long id, HttpServletRequest request) {
        User userBySubject = userService.getUserBySubject(request);

        Book bookFound = readingService.findById(id);

        bookFound.getUserLikes().stream()
                .filter(userLikes -> userLikes.getUserLike().equals(userBySubject))
                .findFirst()
                .ifPresent(userLikes -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Permitido apenas um like por conta!");
                });

        UserLikes userLikes = UserLikes.builder()
                .book(bookFound)
                .userLike(userBySubject)
                .build();

        bookFound.getUserLikes().add(userLikes);
        userLikesRepository.save(userLikes);
    }
}
