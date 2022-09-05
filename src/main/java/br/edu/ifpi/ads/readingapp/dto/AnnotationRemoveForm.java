package br.edu.ifpi.ads.readingapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnotationRemoveForm {
    private Long annotationId;
    private Long bookId;
    private String annotation;
    private Integer order;
}
