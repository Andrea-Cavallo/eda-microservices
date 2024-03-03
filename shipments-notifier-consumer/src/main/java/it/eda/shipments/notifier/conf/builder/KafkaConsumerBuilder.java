package it.eda.shipments.notifier.conf.builder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

@Configuration
public class KafkaConsumerBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerBuilder.class);

    // Definizione delle configurazioni come costanti
    private static final String TOPIC = "notifier.shipmentManagement.shipments";
    private static final String BROKERS = "kafka:29092";
    private static final String GROUP_ID = "notifier-v.0.0.1";
    private static final String AUTO_OFFSET_RESET = "earliest";

    @Bean
    public String buildKafkaEndpoint() {
        String kafkaEndpoint = String.format(
            "kafka:%s?brokers=%s&groupId=%s&keyDeserializer=org.apache.kafka.common.serialization.StringDeserializer" +
            "&valueDeserializer=org.apache.kafka.common.serialization.StringDeserializer&autoOffsetReset=%s",
            TOPIC, BROKERS, GROUP_ID, AUTO_OFFSET_RESET);

        LOGGER.info("Kafka consumer configuration is: {}", kafkaEndpoint);
        return kafkaEndpoint;
    }
}
