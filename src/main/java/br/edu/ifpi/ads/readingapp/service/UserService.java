package br.edu.ifpi.ads.readingapp.service;

import br.edu.ifpi.ads.readingapp.domain.Phone;
import br.edu.ifpi.ads.readingapp.domain.User;
import br.edu.ifpi.ads.readingapp.dto.SignupForm;
import br.edu.ifpi.ads.readingapp.dto.ValidateAccountForm;
import br.edu.ifpi.ads.readingapp.dto.ValidatePhoneForm;
import br.edu.ifpi.ads.readingapp.repository.PhoneRepository;
import br.edu.ifpi.ads.readingapp.repository.UserRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PhoneRepository phoneRepository;
    private static String code = "";

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
        return Optional.ofNullable(userRepository.findByEmail(email)).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email não encontrado")
        );
    }

    public Map<String, String> refreshToken(HttpServletRequest request){
        String authorizationHeader = request.getHeader("Authorization");

        if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")){
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "cabeçalho de autorização não está definido ou o Bearer não está especificado");
        }
        String refreshToken = authorizationHeader.replace("Bearer ", "");

        DecodedJWT decodedJWT;
        String subject;
        try{
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256("COXINHA".getBytes())).build();
            decodedJWT = jwtVerifier.verify(refreshToken);
            subject = decodedJWT.getSubject();
        }catch (JWTVerificationException exception){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }

        User user = userRepository.findByEmail(subject);
        String userRefreshToken = user.getRefreshToken();

        if(!userRefreshToken.equals(refreshToken)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Refresh Token revogado!");
        }

        String newAccessToken = JWT.create()
                .withSubject(user.getEmail())
                .withExpiresAt(Instant.now().plusMillis(60 * 60 * 1000))
                .withIssuedAt(Instant.now())
                .withClaim("name",user.getName())
                .withClaim("phone", user.getPhone().getNumber())
                .sign(Algorithm.HMAC256("COXINHA".getBytes()));

        String newRefreshToken = JWT.create()
                .withSubject(user.getEmail())
                .withExpiresAt(Instant.now().plusMillis((24 * 60) * 60 * 1000))
                .withIssuedAt(Instant.now())
                .withClaim("name",user.getName())
                .withClaim("phone", user.getPhone().getNumber())
                .sign(Algorithm.HMAC256("COXINHA".getBytes()));

        user.setRefreshToken(newRefreshToken);
        userRepository.save(user);

        return Map.of("access-token", newAccessToken, "refresh-token", newRefreshToken);
    }

    public User getUserBySubject(HttpServletRequest request){
        String authorizationHeader = request.getHeader("Authorization");

        if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")){
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "cabeçalho de autorização não está definido ou o Bearer não está especificado");
        }
        String token = authorizationHeader.replace("Bearer ", "");

        DecodedJWT decodedJWT;
        String subject;
        try{
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256("COXINHA".getBytes())).build();
            decodedJWT = jwtVerifier.verify(token);
            subject = decodedJWT.getSubject();
        }catch (JWTVerificationException exception){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }

        return userRepository.findByEmail(subject);
    }

    public String generateCode(){
        code = String.valueOf((int) (Math.random() * 1000000));
        log.info("Código de ativação: {}", code);
        return code;
    }
}
