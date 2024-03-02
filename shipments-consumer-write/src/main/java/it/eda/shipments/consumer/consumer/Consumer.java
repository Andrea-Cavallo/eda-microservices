package it.eda.shipments.consumer.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import it.eda.shipments.consumer.model.ShipmentsMessage;
import it.eda.shipments.consumer.service.ShipmentsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class Consumer {

    private static final Logger logger = LoggerFactory.getLogger(Consumer.class);
    private final ShipmentsService shipmentsService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RetryTemplate retryTemplate;

    public Consumer(ShipmentsService shipmentsService, RetryTemplate retryTemplate) {
        this.shipmentsService = shipmentsService;
        this.retryTemplate = retryTemplate;
    }

    @KafkaListener(topics = "shipment-topic-v0.0.1", containerFactory = "shipmentKafkaListnerContainerFactory", groupId = "shipment-consumer-service")
    public void consume(ConsumerRecord<String, String> consumerRecord, Acknowledgment acknowledgment) {
        String message = consumerRecord.value();
        logger.info("Received message from topic 'shipment-topic': {}", message);

        if (message == null || message.isEmpty()) {
            logger.error("Received empty message");
            acknowledgment.acknowledge();
            return;
        }

        retryTemplate.execute(retryContext -> {
            try {
                ShipmentsMessage shipmentsMessage = objectMapper.readValue(message, ShipmentsMessage.class);
                processMessage(shipmentsMessage);
                acknowledgment.acknowledge();
            } catch (JsonProcessingException e) {
                logger.error("Error while converting message to ShipmentsMessage: {}", message, e);
            }
            return null;
        });
    }

    private void processMessage(ShipmentsMessage shipmentsMessage) {
        if (Boolean.TRUE.equals(shipmentsMessage.getIsUpdate())) {
            logger.info("Updating shipment: {}", shipmentsMessage);
            shipmentsService.updateSpedizione(shipmentsMessage.getSpedizione());
        } else {
            logger.info("Saving new shipment: {}", shipmentsMessage);
            shipmentsService.saveSpedizione(shipmentsMessage.getSpedizione());
        }
    }
}
