package br.edu.ifpi.ads.readingapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidateAccountForm {
    private String code;
    private String email;
}
