package br.edu.ifpi.ads.readingapp.repository;

import br.edu.ifpi.ads.readingapp.domain.Phone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhoneRepository extends JpaRepository<Phone, Long> {
    Phone findByNumber(String number);
}
