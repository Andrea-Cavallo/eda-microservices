package it.eda.shipments.producer.exception;

import io.smallrye.mutiny.Uni;
import it.eda.shipments.producer.controller.model.RestResponse;
import it.eda.shipments.producer.controller.model.SpedizioneDTO;
import it.eda.shipments.producer.validator.ValidationResult;
import jakarta.ws.rs.core.Response;

import java.util.Map;

public class ExceptionUtils {

    private ExceptionUtils() {}

    public static Uni<Response> buildResponseForUnathorizedUser(RestResponse<SpedizioneDTO> response) {
        response.setOutput(null);
        response.setCodiceErrore(Map.of("codice",401L));
        response.setDescrizioneErrore(Map.of("descrizione", "Utente non autorizzato ad accedere alla risorsa"));
        return Uni.createFrom().item(Response.status(Response.Status.UNAUTHORIZED).entity(response).build());
    }

    public static Uni<Response> buildResponseForValidationException(ValidationResult bodyValidationResult, RestResponse<SpedizioneDTO> response) {
        String errorDescription = String.join(", ", bodyValidationResult.getErrors());
        response.setCodiceErrore(Map.of("codice",400L));
        response.setDescrizioneErrore(Map.of("descrizione",  errorDescription));
        return Uni.createFrom().item(Response.status(Response.Status.BAD_REQUEST).entity(response).build());
    }
}
