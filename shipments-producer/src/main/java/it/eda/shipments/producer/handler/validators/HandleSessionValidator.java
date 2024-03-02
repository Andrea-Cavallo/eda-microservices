package it.eda.shipments.producer.handler.validators;

import io.opentelemetry.api.trace.Span;
import io.smallrye.mutiny.Uni;
import it.eda.shipments.producer.controller.model.SpedizioneDTO;
import it.eda.shipments.producer.service.RedisSessionService;
import it.eda.shipments.producer.validator.ValidationResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Objects;

@ApplicationScoped
@Slf4j
public class HandleSessionValidator {

    @Inject
    RedisSessionService redisSessionService;

    /**
     * Validates a session ID, per adesso è un mock giusto per simulare la presenza della validazione del sessionId
     * chiamante, e per rendere sicure le API
     *
     * @param sessionId The ID of the session da validare.
     * @param span      The span for tracing.
     * @return una ValidationResult Wrappata nell'oggetto Uni
     */
    public Uni<ValidationResult> validateSession(String sessionId, Span span, SpedizioneDTO spedizioneReq, Boolean isToUpdate) {
        boolean isValid = sessionId != null && !sessionId.isEmpty();
        if (isValid) {
            return checkIfEmailProvidedIsStored(sessionId, spedizioneReq.getUserEmail(), isToUpdate)
                    .map(emailIsValid -> {
                        if (Boolean.FALSE.equals(emailIsValid)) {
                            return new ValidationResult(false, Collections.emptyList());
                        }
                        log.info("la spedizione puo' essere evasa per sessionId {} e email {}", sessionId, spedizioneReq.getUserEmail());

                        return new ValidationResult(true, Collections.emptyList());
                    });
        } else {
            return Uni.createFrom().item(new ValidationResult(false, Collections.emptyList()));
        }
    }

    private Uni<Boolean> checkIfEmailProvidedIsStored(String sessionId, String email, Boolean isToUpdate) {
        return redisSessionService.getSession(sessionId).map(s -> {
            if (s.isPresent() && !Objects.isNull(s.get().getEmail())) {
                String emailStored = s.get().getEmail();
                log.info("email salvata su redis: {}, email nel requestBody: {}, sessionId: {}", emailStored, email, sessionId);

                if (Boolean.TRUE.equals(isToUpdate)) {
                    String role = s.get().getRole();
                    log.info("bisogna verificare che l utente sia anche admin, il ruolo dell'utente che sta tentando l'accesso: {}", role);
                    if (!"admin".equalsIgnoreCase(role)) {
                        log.warn("Attenzione l'utente con email {} non ha il ruolo di admin e quindi non puo' accedere all'aggiornamento", emailStored);

                        return false;
                    }

                }
                return emailStored.trim().equalsIgnoreCase(email.trim());

            }
            log.error("Nota l'email {} non è associata al sessionId {}", email, sessionId);

            return false;
        });
    }
}
