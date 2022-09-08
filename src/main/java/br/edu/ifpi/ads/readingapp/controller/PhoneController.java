package br.edu.ifpi.ads.readingapp.controller;

import br.edu.ifpi.ads.readingapp.dto.ValidatePhoneForm;
import br.edu.ifpi.ads.readingapp.service.PhoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PhoneController {

    private final PhoneService phoneService;

    @GetMapping("/validation-code/phone")
    public ResponseEntity<String> solicitarPhoneCode(){
        String code = phoneService.generatePhoneCode();
        return new ResponseEntity<>(code , HttpStatus.ACCEPTED);
    }

    @PostMapping("/validate/phone")
    public ResponseEntity<Void> validatePhone(@RequestBody ValidatePhoneForm validatePhoneForm){
        phoneService.validatePhone(validatePhoneForm);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
