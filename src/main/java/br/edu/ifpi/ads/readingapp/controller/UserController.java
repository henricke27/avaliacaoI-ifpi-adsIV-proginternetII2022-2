package br.edu.ifpi.ads.readingapp.controller;

import br.edu.ifpi.ads.readingapp.dto.SignupForm;
import br.edu.ifpi.ads.readingapp.dto.ValidateAccountForm;
import br.edu.ifpi.ads.readingapp.dto.ValidatePhoneForm;
import br.edu.ifpi.ads.readingapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupForm signupForm){
        return new ResponseEntity<>(userService.signup(signupForm), HttpStatus.ACCEPTED);
    }

    @PostMapping("/validate/account")
    public ResponseEntity<Void> validateAccount(@RequestBody ValidateAccountForm validateAccountForm){
        userService.validateAccount(validateAccountForm);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/validation-code/phone")
    public ResponseEntity<String> solicitarPhone(){
        String code = userService.generateCode();
        return new ResponseEntity<>(code ,HttpStatus.ACCEPTED);
    }

    @PostMapping("/validate/phone")
    public ResponseEntity<Void> validatePhone(@RequestBody ValidatePhoneForm validatePhoneForm){
        userService.validatePhone(validatePhoneForm);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
