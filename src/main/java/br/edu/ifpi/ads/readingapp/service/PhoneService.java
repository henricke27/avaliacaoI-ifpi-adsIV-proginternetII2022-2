package br.edu.ifpi.ads.readingapp.service;

import br.edu.ifpi.ads.readingapp.domain.Phone;
import br.edu.ifpi.ads.readingapp.dto.ValidatePhoneForm;
import br.edu.ifpi.ads.readingapp.repository.PhoneRepository;
import br.edu.ifpi.ads.readingapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class PhoneService {

    private final PhoneRepository phoneRepository;
    private static String phoneCode = "";

    public Phone save(Phone phone){
        return phoneRepository.save(phone);
    }

    public Phone findByNumber(String phone) {
        return phoneRepository.findByNumber(phone);
    }

    public void validatePhone(ValidatePhoneForm validatePhoneForm) {
        if (!validatePhoneForm.getCode().equals(phoneCode)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Código inválido");
        }
        Phone phone = findByNumber(validatePhoneForm.getPhone());

        if (phone == null || phone.getEnable().equals(true)){
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Número de telefone inválido para ativação");
        }

        phone.setEnable(true);
        save(phone);
    }

    public String generatePhoneCode(){
        phoneCode = String.valueOf((int) (Math.random() * 1000000));
        log.info("Código de ativação: {}", phoneCode);
        return phoneCode;
    }
}
