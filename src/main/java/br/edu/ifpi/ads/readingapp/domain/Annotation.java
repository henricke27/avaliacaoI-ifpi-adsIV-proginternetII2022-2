package br.edu.ifpi.ads.readingapp.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "annotations")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Annotation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String annotation;
    private Integer order;
    @ManyToOne
    private Book book;
}
