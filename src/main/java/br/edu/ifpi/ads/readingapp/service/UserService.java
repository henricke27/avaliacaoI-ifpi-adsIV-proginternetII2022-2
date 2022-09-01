package br.edu.ifpi.ads.readingapp.service;

import br.edu.ifpi.ads.readingapp.domain.Phone;
import br.edu.ifpi.ads.readingapp.domain.User;
import br.edu.ifpi.ads.readingapp.dto.SignupForm;
import br.edu.ifpi.ads.readingapp.dto.ValidateAccountForm;
import br.edu.ifpi.ads.readingapp.dto.ValidatePhoneForm;
import br.edu.ifpi.ads.readingapp.repository.PhoneRepository;
import br.edu.ifpi.ads.readingapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PhoneRepository phoneRepository;
    private static String code = "";

    @Transactional(rollbackFor = Exception.class)
    public String signup(SignupForm signupForm){
        User userFound = userRepository.findByEmail(signupForm.getEmail());
        Phone phoneFound = phoneRepository.findByNumber(signupForm.getPhone());

        Optional.ofNullable(userFound).ifPresent((user) -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email já cadastrado!");
        });

        Optional.ofNullable(phoneFound).ifPresent((phone) -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Número de telefone já cadastrado!");
        });

        Phone phone = Phone.builder()
                .number(signupForm.getPhone())
                .enable(false)
                .build();

        Phone phoneSaved = phoneRepository.save(phone);

        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

        User user = User.builder()
                .email(signupForm.getEmail())
                .password(passwordEncoder.encode(signupForm.getPassword()))
                .name(signupForm.getName())
                .phone(phoneSaved)
                .enable(false)
                .build();

        userRepository.save(user);
        return generateCode();
    }
    @Transactional(rollbackFor = Exception.class)
    public void validateAccount(ValidateAccountForm validateAccountForm) {
        if (!validateAccountForm.getCode().equals(code)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Código inválido");
        }
        String email = validateAccountForm.getEmail();
        User userFound = userRepository.findByEmail(email);

        if (userFound == null || userFound.getEnable().equals(true)){
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Email inválido para ativação");
        }

        userFound.setEnable(true);
        userRepository.save(userFound);
    }

    @Transactional(rollbackFor = Exception.class)
    public void validatePhone(ValidatePhoneForm validatePhoneForm) {
        if (!validatePhoneForm.getCode().equals(code)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Código inválido");
        }
        Phone phone = phoneRepository.findByNumber(validatePhoneForm.getPhone());

        if (phone == null || phone.getEnable().equals(true)){
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Número de telefone inválido para ativação");
        }

        phone.setEnable(true);
        phoneRepository.save(phone);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User userFound = Optional.ofNullable(userRepository.findByEmail(email)).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email não encontrado")
        );

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .builder()
                .username(userFound.getEmail())
                .password(userFound.getPassword())
                .roles("USER")
                .build();

        return userDetails;
    }
    public String generateCode(){
        code = String.valueOf((int) (Math.random() * 1000000));
        log.info("Código de ativação: {}", code);
        return code;
    }
}
