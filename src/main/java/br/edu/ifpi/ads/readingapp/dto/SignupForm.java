package br.edu.ifpi.ads.readingapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupForm {
    private String email;
    private String password;
    private String name;
    private String phone;
}
