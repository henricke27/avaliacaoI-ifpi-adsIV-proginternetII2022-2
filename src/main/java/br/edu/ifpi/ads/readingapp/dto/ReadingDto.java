package br.edu.ifpi.ads.readingapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadingDto {
    private String holder;
    private String title;
    private List<String> annotations;
    private List<String> likes;
    private Integer page;
    private Boolean concluded;
    private Boolean stopped;
}
