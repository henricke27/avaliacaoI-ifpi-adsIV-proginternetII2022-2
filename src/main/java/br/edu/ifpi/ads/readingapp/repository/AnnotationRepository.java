package br.edu.ifpi.ads.readingapp.repository;

import br.edu.ifpi.ads.readingapp.domain.Annotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnnotationRepository extends JpaRepository<Annotation, Long> {
}
