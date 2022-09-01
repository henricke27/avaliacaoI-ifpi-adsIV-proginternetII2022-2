package br.edu.ifpi.ads.readingapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidatePhoneForm {
    private String code;
    private String phone;
}
