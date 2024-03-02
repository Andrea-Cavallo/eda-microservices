package it.eda.shipments.producer.handler;



import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.smallrye.mutiny.Uni;
import it.eda.shipments.producer.controller.model.RestResponse;
import it.eda.shipments.producer.controller.model.SpedizioneDTO;

import it.eda.shipments.producer.exception.ExceptionUtils;
import it.eda.shipments.producer.handler.validators.HandleBodyValidator;
import it.eda.shipments.producer.handler.validators.HandleSessionValidator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class RequestHandler {

    public RequestHandler(HandleSessionValidator sessionValidator, HandleBodyValidator requestBodyValidator, HandleValidRequest requestProcessor, Tracer tracer) {
        this.sessionValidator = sessionValidator;
        this.requestBodyValidator = requestBodyValidator;
        this.requestProcessor = requestProcessor;
        this.tracer = tracer;
    }

    private final HandleSessionValidator sessionValidator;

    private final HandleBodyValidator requestBodyValidator;

    private final HandleValidRequest requestProcessor;

    private final Tracer tracer ;

    /**
     * Gestisce la spedizione, validando prima la sessione e poi il corpo della richiesta.
     * Utilizza {@link Uni} per modellare operazioni asincrone, consentendo la trasformazione
     * e la gestione dei risultati in modo reattivo.
     *
     * @param spedizioneReq L'oggetto richiesta contenente i dati della spedizione.
     * @param isToUpdate    Indica se la spedizione deve essere aggiornata.
     * @param sessionId     Identificativo della sessione da validare.
     * @return Un {@link Uni<Response>} che emette la risposta HTTP risultante dall'elaborazione
     * della richiesta. Se la validazione della sessione fallisce, emette una risposta
     * HTTP 401 Unauthorized con il messaggio "Session ID non valido". Se la validazione
     * della sessione ha successo, procede con la validazione del corpo della richiesta stessa cosa se cè qualche errore
     * viene tornato un .
     * In caso di successo, procede con l'elaborazione della richiesta di spedizione.
     * Utilizza {@link Uni transformToUni(java.util.function.Function)} per trasformare
     * l'elemento emesso da questo Uni, permettendo la catenazione delle operazioni
     * di validazione e elaborazione in modo asincrono e reattivo.
     */
    public Uni<Response> handleShippment(SpedizioneDTO spedizioneReq, Boolean isToUpdate, String sessionId) {
        Span span = tracer.spanBuilder ("handleShipment").startSpan();
        RestResponse<SpedizioneDTO> response = new RestResponse<>();
        log.info("Handle Shippment incoming request-> spedizioneRequest {}, isToUpdate {}, sessionId {}", spedizioneReq, isToUpdate,sessionId);

        return sessionValidator.validateSession(sessionId, span,spedizioneReq,isToUpdate)
                .onItem().transformToUni(sessionValidationResult -> {
                    if (!sessionValidationResult.isValid()) {
                        return ExceptionUtils.buildResponseForUnathorizedUser(response);
                    } else {
                        return requestBodyValidator.validateRequestBody(spedizioneReq, isToUpdate)
                                .onItem().transformToUni(bodyValidationResult -> {
                                    if (!bodyValidationResult.isValid()) {
                                        return ExceptionUtils.buildResponseForValidationException(bodyValidationResult, response);
                                    } else {
                                        return requestProcessor.processRequest(spedizioneReq, isToUpdate);
                                    }
                                });
                    }
                })
                .onTermination().invoke(span::end);
    }


}
