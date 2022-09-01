package br.edu.ifpi.ads.readingapp.filter;

import br.edu.ifpi.ads.readingapp.domain.User;
import br.edu.ifpi.ads.readingapp.repository.UserRepository;
import br.edu.ifpi.ads.readingapp.service.UserService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;
    private UserService userService;
    private UserRepository userRepository;
    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, UserService userService, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.userRepository = userRepository;
        setFilterProcessesUrl("/signin");
    }
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try {
            User userIncomplete = new ObjectMapper().readValue(request.getInputStream(), User.class);

            User userFound = userRepository.findByEmail(userIncomplete.getUsername());

            if (userFound.getEnable().equals(false)){
                throw new RuntimeException("Conta inativa");
            }

            Authentication token = new UsernamePasswordAuthenticationToken
                    (userFound.getEmail(), userFound.getPassword(), new ArrayList<>());
            return authenticationManager.authenticate(token);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        User user = (User) authResult.getPrincipal();

        String accessToken = JWT.create()
                .withSubject(user.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + 60 * 60 * 1000))
                .withClaim("name",user.getName())
                .withClaim("phone", user.getPhone().getNumber())
                .sign(Algorithm.HMAC256("COXINHA".getBytes()));

        String refreshToken = JWT.create()
                .withClaim("name",user.getName())
                .withClaim("phone", user.getPhone().getNumber())
                .withExpiresAt(new Date(System.currentTimeMillis() + (24 * 60) * 60 * 1000))
                .sign(Algorithm.HMAC256("COXINHA".getBytes()));

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        String tokens = new ObjectMapper().writeValueAsString(
                Map.of("access-token",accessToken, "refresh-token",refreshToken));

        response.getOutputStream().write(tokens.getBytes());
    }

}
