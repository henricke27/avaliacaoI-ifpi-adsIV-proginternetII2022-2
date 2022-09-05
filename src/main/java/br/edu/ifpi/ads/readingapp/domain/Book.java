package br.edu.ifpi.ads.readingapp.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User holder;
    private String title;
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<Annotation> annotations;
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<UserLikes> userLikes;
    private Integer page;
    private Boolean concluded;
    private Boolean stopped;


}
