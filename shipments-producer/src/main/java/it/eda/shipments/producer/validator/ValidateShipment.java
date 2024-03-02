package it.eda.shipments.producer.validator;


import it.eda.shipments.producer.controller.model.SpedizioneDTO;
import it.eda.shipments.producer.utils.OrderProducerUtils;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Componente per la validazione dei dettagli della spedizione. Fornisce metodi
 * per validare vari campi di un oggetto SpedizioneDTO.
 */
@ApplicationScoped
public class ValidateShipment {

    /**
     * Validata l'oggetto spedizione passato in ingresso, e in base alla discriminante flag isToUpdate
     * valida per create o per update
     *
     * @param spedizioneDTO L 'oggetto da validare
     * @param isToUpdate    Boolean flag per capire se proviene dalla create o dall'update
     * @return Una List<String> di error messages  se ci sono altrimenti una lista vuota
     */
    public List<String> validate(SpedizioneDTO spedizioneDTO, Boolean isToUpdate) {
        List<String> errorMessages = new ArrayList<>();

        if (Boolean.TRUE.equals(isToUpdate)) {
            if (spedizioneDTO == null) {
                errorMessages.add("input body Null");
                return errorMessages;
            }
            if (spedizioneDTO.getUserEmail() != null) {
                checkEmailFormat(spedizioneDTO.getUserEmail(), errorMessages);
            }
            if (spedizioneDTO.getCap() != null) {
                checkCapFormat(spedizioneDTO.getCap(), errorMessages);
            }
        } else {
            validateFieldsForCreate(spedizioneDTO, errorMessages);
        }

        return errorMessages;
    }

    private void validateFieldsForCreate(SpedizioneDTO spedizioneDTO, List<String> errorMessages) {
        if (spedizioneDTO == null) {
            errorMessages.add("Il body in input non può essere null");
            return;
        }
        checkMandatoryFields(spedizioneDTO.getUserEmail(), "Email", errorMessages);
        checkMandatoryFields(spedizioneDTO.getCap(), "Cap", errorMessages);
        checkMandatoryFields(spedizioneDTO.getIndirizzo(), "Indirizzo", errorMessages);

        checkEmailFormat(spedizioneDTO.getUserEmail(), errorMessages);
        checkCapFormat(spedizioneDTO.getCap(), errorMessages);
    }

    private void checkMandatoryFields(Object field, String fieldName, List<String> errorMessages) {
        if (OrderProducerUtils.isFieldMissing(field)) {
            errorMessages.add("Campo obbligatorio: " + fieldName);
        }
    }

    private void checkEmailFormat(String email, List<String> errorMessages) {
        if (!OrderProducerUtils.isValidEmail(email) && !Objects.isNull(email)) {
            errorMessages.add("Formato email non valido per: " + email);
        }
    }

    private void checkCapFormat(String cap, List<String> errorMessages) {
        if (!OrderProducerUtils.checkIfCapIsValid(cap) && !Objects.isNull(cap)) {
            errorMessages.add("Formato CAP non valido: " + cap + ", deve essere un numero di 5 caratteri ");
        }
    }
}
