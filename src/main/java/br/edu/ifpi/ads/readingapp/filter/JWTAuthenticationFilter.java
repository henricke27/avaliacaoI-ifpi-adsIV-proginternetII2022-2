package br.edu.ifpi.ads.readingapp.filter;

import br.edu.ifpi.ads.readingapp.domain.User;
import br.edu.ifpi.ads.readingapp.repository.UserRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;

@Log4j2
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        setFilterProcessesUrl("/signin");
        setUsernameParameter("email");
    }
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try {
            User userIncomplete = new ObjectMapper().readValue(request.getInputStream(), User.class);
            User userFound = userRepository.findByEmail(userIncomplete.getEmail());

            log.info("User {}", userFound);

            if (userFound == null){
                throw new RuntimeException("Usuario não encontrado");
            }
            if(userFound.getEnable().equals(false)){
                throw new RuntimeException("Conta inativa");
            }

            Authentication token = new UsernamePasswordAuthenticationToken
                    (userIncomplete.getEmail(), userIncomplete.getPassword(), new ArrayList<>());
            return authenticationManager.authenticate(token);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        User user = (User) authResult.getPrincipal();

        String accessToken = JWT.create()
                .withSubject(user.getEmail())
                .withExpiresAt(Instant.now().plusMillis(60 * 60 * 1000))
                .withIssuedAt(Instant.now())
                .withClaim("name",user.getName())
                .withClaim("phone", user.getPhone().getNumber())
                .sign(Algorithm.HMAC256("COXINHA".getBytes()));

        String refreshToken = JWT.create()
                .withSubject(user.getEmail())
                .withExpiresAt(Instant.now().plusMillis((24 * 60) * 60 * 1000))
                .withIssuedAt(Instant.now())
                .withClaim("name",user.getName())
                .withClaim("phone", user.getPhone().getNumber())
                .sign(Algorithm.HMAC256("COXINHA".getBytes()));

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        String tokens = new ObjectMapper().writeValueAsString(
                Map.of("access-token",accessToken, "refresh-token",refreshToken));

        response.getOutputStream().write(tokens.getBytes());
    }

}
