package br.edu.ifpi.ads.readingapp.repository;

import br.edu.ifpi.ads.readingapp.domain.Phone;
import br.edu.ifpi.ads.readingapp.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    User findByPhone(Phone phone);
}
