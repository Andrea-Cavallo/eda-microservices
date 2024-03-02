package it.eda.shipments.notifier.processor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

@Component
public class DeserializeProcessor implements Processor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeserializeProcessor.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public void process(Exchange exchange) throws Exception {
        try {
            String messageBody = exchange.getMessage().getBody(String.class);
            LOGGER.info("Processing message: {}", messageBody);

            // Rimuovere le escape delle virgolette se presente
            messageBody = messageBody.replace("\\\"", "\"");

            // Convertire il JSON in Map
            @SuppressWarnings("unchecked")
			Map<String, Object> messageMap = OBJECT_MAPPER.readValue(messageBody, Map.class);
            // Potresti voler fare un ulteriore parsing qui, a seconda di come vuoi utilizzare la mappa

            // Esempio di come accedere a valori specifici se necessario
            @SuppressWarnings("unchecked")
			Map<String, Object> fullDocument = (Map<String, Object>) messageMap.get("fullDocument");
            if (fullDocument != null) {
                String stato = (String) fullDocument.get("stato");
                String destinatario = (String) fullDocument.get("destinatario");
                String userEmail = (String) fullDocument.get("userEmail");

                // Impostare i valori come header se necessario
                exchange.getIn().setHeader("stato", stato);
                exchange.getIn().setHeader("destinatario", destinatario);
                exchange.getIn().setHeader("userEmail", userEmail);
            }

        } catch (Exception e) {
            LOGGER.error("Error processing message", e);
            throw e;
        }
    }
}

