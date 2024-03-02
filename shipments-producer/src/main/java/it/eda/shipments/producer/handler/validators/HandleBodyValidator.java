package it.eda.shipments.producer.handler.validators;

import io.smallrye.mutiny.Uni;
import it.eda.shipments.producer.controller.model.SpedizioneDTO;
import it.eda.shipments.producer.validator.ValidateShipment;
import it.eda.shipments.producer.validator.ValidationResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class HandleBodyValidator {

    private final ValidateShipment validateShipment;

    @Inject
    public HandleBodyValidator(ValidateShipment validateShipment) {
        this.validateShipment = validateShipment;
    }

    /**
     * Validates la request in ingresso, viene fatto un controllo sulla correttezza dei campi in ingresso.
     *
     * @param spedizioneReq La richiesta della spedizione
     * @param isToUpdate    flag che ci permette di capire se proviene dalla create o dall update.
     * @param span          span per il tracing.
     * @return Un oggetto wrappato in Uni , ValidationResult che ha due campi un Boolean per capire se è valido e una eventuale lista di errori .
     */
    public Uni<ValidationResult> validateRequestBody(SpedizioneDTO spedizioneReq, Boolean isToUpdate) {
        List<String> validationErrors = validateShipment.validate(spedizioneReq, isToUpdate);
        return Uni.createFrom().item(new ValidationResult(validationErrors.isEmpty(), validationErrors));
    }
}
