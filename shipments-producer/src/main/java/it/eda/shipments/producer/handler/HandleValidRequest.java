package it.eda.shipments.producer.handler;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.mutiny.Uni;
import it.eda.shipments.producer.controller.model.RestResponse;
import it.eda.shipments.producer.controller.model.SpedizioneDTO;
import it.eda.shipments.producer.controller.model.enums.StatoEnum;
import it.eda.shipments.producer.exception.JsonException;
import it.eda.shipments.producer.exception.KafkaProducerException;
import it.eda.shipments.producer.mapper.SpedizioneMapper;
import it.eda.shipments.producer.message.SpedizioneMessage;
import it.eda.shipments.producer.service.SpedizioneTopicProducer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

import java.util.Collections;
import java.util.Map;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;

@ApplicationScoped
public class HandleValidRequest {

    private final SpedizioneMapper spedizioneMapper;
    private final SpedizioneTopicProducer spedizioneTopicProducer;
    private final Tracer tracer;

    @Inject
    ObjectMapper objectMapper;


    @Inject
    public HandleValidRequest(SpedizioneMapper spedizioneMapper, SpedizioneTopicProducer spedizioneTopicProducer, Tracer tracer) {
        this.spedizioneMapper = spedizioneMapper;
        this.spedizioneTopicProducer = spedizioneTopicProducer;
        this.tracer = tracer;
    }

    public Uni<Response> processRequest(SpedizioneDTO spedizioneReq, Boolean isToUpdate) {
        Span span = tracer.spanBuilder("processValidRequest").startSpan();
        try {
            RestResponse<SpedizioneDTO> response = new RestResponse<>();

            if (Boolean.FALSE.equals( isToUpdate )) {
                spedizioneReq.setStato(StatoEnum.PENDING);

            }
            else {
                spedizioneReq.setStato(spedizioneReq.getStato());

            }
            SpedizioneMessage topic = spedizioneMapper.toSpedizioneMessage(spedizioneReq);
            topic.setIsUpdate(isToUpdate);
            String serializedSpedizioneMessage = objectMapper.writeValueAsString((topic));


            try {
                spedizioneTopicProducer.send(serializedSpedizioneMessage);
            } catch (KafkaProducerException kE) {
                response.setOutput(null);
                response.setCodiceErrore(Map.of("codice", 500L));
                response.setDescrizioneErrore(Map.of("descrizione", "errore durante la produzione del topic"));
                response.setDescrizioneErrore(Map.of("ulterioriDettagli", kE.getMessage()));
                return Uni.createFrom().item(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build());
            }
            response.setOutput(topic.getSpedizione());
            response.setDescrizioneErrore(Collections.emptyMap());
            response.setCodiceErrore(Collections.emptyMap());
            return Uni.createFrom().item(Response.status(Response.Status.CREATED).entity(response).build());
        } catch (JsonProcessingException e) {
            throw new JsonException("Erorre durante la serializzazione dell'oggetto in topic", e);
        } finally {
            span.end();
        }
    }
}
