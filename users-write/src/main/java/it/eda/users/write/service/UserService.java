package it.eda.users.write.service;

import static it.eda.users.write.utils.Constants.CAMPI_OBBLIGATORI_MANCANTI;
import static it.eda.users.write.utils.Constants.CODICE;
import static it.eda.users.write.utils.Constants.DESCRIZIONE;
import static it.eda.users.write.utils.Constants.EMAIL_E_PASSWORD_SONO_OBBLIGATORIE;
import static it.eda.users.write.utils.Constants.ERRORE_DURANTE_IL_SALVATAGGIO_DELL_UTENTE;
import static it.eda.users.write.utils.Constants.LOGIN_RIUSCITO;
import static it.eda.users.write.utils.Constants.NESSUN_UTENTE_TROVATO;
import static it.eda.users.write.utils.Constants.PASSWORD_ERRATA;
import static it.eda.users.write.utils.Constants.UTENTE_SALVATO_CON_SUCCESSO;

import java.util.Collections;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.eda.users.write.model.RestResponse;
import it.eda.users.write.model.Utente;
import it.eda.users.write.repo.UserRepository;

@Service
public class UserService {

	private static final Logger log = LoggerFactory.getLogger(UserService.class);

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public ResponseEntity<RestResponse<Utente>> register(Utente user) {
		if (user.getCognome() == null || user.getNome() == null || user.getEmail() == null
				|| user.getPassword() == null) {
			log.warn("Attenzione: " + CAMPI_OBBLIGATORI_MANCANTI);
			return buildResponse(null, CAMPI_OBBLIGATORI_MANCANTI, 400);
		}
		log.info("Register user {}", user);

		try {
			log.debug("Cripto password...");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			Utente savedUser = userRepository.save(user);
			log.debug("Utente correttamente salvato su db");

			return buildResponse(savedUser, UTENTE_SALVATO_CON_SUCCESSO, HttpStatus.OK.value());
		} catch (DataAccessException e) {
			log.error("Attenzione: " + ERRORE_DURANTE_IL_SALVATAGGIO_DELL_UTENTE);

			return buildResponse(null, ERRORE_DURANTE_IL_SALVATAGGIO_DELL_UTENTE,
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	public ResponseEntity<RestResponse<Utente>> login(Utente user) {
		if (Objects.isNull(user.getEmail()) || Objects.isNull(user.getPassword())) {
			log.warn(EMAIL_E_PASSWORD_SONO_OBBLIGATORIE + " per il login");
			return buildResponse(null, EMAIL_E_PASSWORD_SONO_OBBLIGATORIE, HttpStatus.BAD_REQUEST.value());
		}

		return userRepository.findFirstByEmail(user.getEmail()).map(foundUser -> {
			if (passwordEncoder.matches(user.getPassword(), foundUser.getPassword())) {
				log.info("Utente trovato, con pwd corretta, login riuscito..");
				return buildResponse(foundUser, LOGIN_RIUSCITO, HttpStatus.OK.value());
			} else {
				log.warn("Utente trovato, pwd sbagliata ..");
				return buildResponse(null, PASSWORD_ERRATA, HttpStatus.UNAUTHORIZED.value());
			}
		}).orElseGet(() -> {
			log.warn(NESSUN_UTENTE_TROVATO);
			return buildResponse(null, NESSUN_UTENTE_TROVATO, HttpStatus.NOT_FOUND.value());
		});
	}

	private ResponseEntity<RestResponse<Utente>> buildResponse(Utente user, String message, int statusCode) {
		RestResponse<Utente> response = new RestResponse<>(user, Collections.singletonMap(DESCRIZIONE, message),
				Collections.singletonMap(CODICE, (long) statusCode));
		return new ResponseEntity<>(response, HttpStatus.valueOf(statusCode));
	}
}
