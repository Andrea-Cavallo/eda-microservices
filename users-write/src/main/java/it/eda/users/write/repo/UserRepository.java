package it.eda.users.write.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import it.eda.users.write.model.Utente;

public interface UserRepository extends JpaRepository<Utente, Long> {
	Optional<Utente> findFirstByEmail(String email);
}
