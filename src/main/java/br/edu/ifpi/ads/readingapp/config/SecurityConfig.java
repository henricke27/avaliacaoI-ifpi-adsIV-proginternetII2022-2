package br.edu.ifpi.ads.readingapp.config;

import br.edu.ifpi.ads.readingapp.filter.JWTAuthenticationFilter;
import br.edu.ifpi.ads.readingapp.filter.JWTAuthorizationFilter;
import br.edu.ifpi.ads.readingapp.repository.UserRepository;
import br.edu.ifpi.ads.readingapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserService userService;
    private final UserRepository userRepository;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        AuthenticationManager authenticationManager = buildAuthenticationManager(http);

        http.csrf().disable()
                .authenticationManager(authenticationManager)
                .authorizeHttpRequests()
                .antMatchers("/signin","/signup","/validate/account","/validation-code/phone").permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .addFilter(new JWTAuthenticationFilter(authenticationManager, userService, userRepository))
                .addFilter(new JWTAuthorizationFilter(authenticationManager));;

        return http.build();
    }

    private AuthenticationManager buildAuthenticationManager(HttpSecurity http) throws Exception {
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

        AuthenticationManagerBuilder managerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        managerBuilder.userDetailsService(userService).passwordEncoder(passwordEncoder);

        return managerBuilder.build();
    }
}
