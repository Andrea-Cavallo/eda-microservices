package it.eda.shipments.producer.service;


import io.vertx.core.Future;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;
import it.eda.shipments.producer.conf.KafkaProducerInitializer;
import it.eda.shipments.producer.exception.KafkaProducerException;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@ApplicationScoped
public class SpedizioneTopicProducer {

    private static final Logger logger = LoggerFactory.getLogger(SpedizioneTopicProducer.class);

    private final KafkaProducerInitializer kafkaProducerInitializer;


    @ConfigProperty(name = "kafka.producer.topic")
    String topic;
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY = 1000;

    public SpedizioneTopicProducer(KafkaProducerInitializer kafkaProducerInitializer) {
        this.kafkaProducerInitializer = kafkaProducerInitializer;

    }

    public void send(String message) {
        sendWithRetry(message, 0);
    }

    private void sendWithRetry(String message, int attempt) {
        KafkaProducer<String, String> kafkaProducer = kafkaProducerInitializer.getKafkaProducer();
        KafkaProducerRecord<String, String> producerRecord = KafkaProducerRecord.create(topic, message);



        Future<io.vertx.kafka.client.producer.RecordMetadata> future = kafkaProducer.send(producerRecord);
        future.onComplete(asyncResult -> {
            if (asyncResult.succeeded()) {
                io.vertx.kafka.client.producer.RecordMetadata metadata = asyncResult.result();
                logger.info("Messaggio inviato correttamente al topic {}, partizione {}, offset {}", metadata.getTopic(), metadata.getPartition(), metadata.getOffset());
            } else {
                if (attempt < MAX_RETRIES) {
                    logger.error("Tentativo {} di {} fallito, errore durante l'invio del messaggio. Nuovo tentativo dopo {}ms", attempt + 1, MAX_RETRIES, RETRY_DELAY, asyncResult.cause());
                    try {
                        Thread.sleep(RETRY_DELAY);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        logger.error("Interruzione durante l'attesa per il retry", e);
                    }
                    sendWithRetry(message, attempt + 1);
                } else {
                    logger.error("Errore durante l'invio del messaggio dopo {} tentativi. Nessun altro tentativo verrà effettuato.", MAX_RETRIES, asyncResult.cause());
                    throw new KafkaProducerException("Impossibile inviare il messaggio dopo " + MAX_RETRIES + " tentativi", asyncResult.cause());
                }
            }
        });
    }
}
