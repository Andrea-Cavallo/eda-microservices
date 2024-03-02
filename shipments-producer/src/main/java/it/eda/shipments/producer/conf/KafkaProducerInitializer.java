package it.eda.shipments.producer.conf;



import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.mutiny.core.Vertx;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class KafkaProducerInitializer {
    @ConfigProperty(name = "kafka.producer.bootstrap-servers")
    String bootstrapServers;

    @ConfigProperty(name = "kafka.producer.key-serializer")
    String keySerializer;

    @ConfigProperty(name = "kafka.producer.value-serializer")
    String valueSerializer;

    @ConfigProperty(name = "kafka.producer.retries")
    int retries;

    @ConfigProperty(name = "kafka.producer.acks")
    String acks;

    @ConfigProperty(name = "kafka.producer.compression-type")
    String compressionType;

    @ConfigProperty(name = "kafka.producer.linger-ms")
    int lingerMs;

    @ConfigProperty(name = "kafka.producer.batch-size")
    int batchSize;

    @ConfigProperty(name = "kafka.producer.buffer-memory")
    long bufferMemory;

    @ConfigProperty(name = "kafka.producer.enable-idempotence")
    boolean enableIdempotence;

    @ConfigProperty(name = "kafka.producer.max-in-flight-requests-per-connection")
    int maxInFlightRequestsPerConnection;


    private final Vertx vertx;


    @Getter
    private KafkaProducer<String, String> kafkaProducer;

    public KafkaProducerInitializer(Vertx vertx) {
        this.vertx = vertx;

    }
    /**
     * Inizializza il produttore Kafka con la configurazione specificata.
     * Questo metodo imposta la configurazione standard del produttore Kafka
     * utilizzando le proprietà definite nella classe e crea un KafkaProducer
     * utilizzando il Vertx fornito.
     *
     * @throws Exception Se si verifica un errore durante l'inizializzazione del produttore Kafka.
     */
    @PostConstruct
    void initialize() {
        Map<String, String> config = new HashMap<>();
        // Configurazione standard del producer Kafka
        config.put("bootstrap.servers", bootstrapServers);
        config.put("key.serializer", keySerializer);
        config.put("value.serializer", valueSerializer);
        config.put("acks", acks);
        config.put("retries", String.valueOf(retries));
        config.put("compression.type", compressionType);
        config.put("linger.ms", String.valueOf(lingerMs));
        config.put("batch.size", String.valueOf(batchSize));
        config.put("buffer.memory", String.valueOf(bufferMemory));
        config.put("enable.idempotence", String.valueOf(enableIdempotence));
        config.put("max.in.flight.requests.per.connection", String.valueOf(maxInFlightRequestsPerConnection));
        config.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, TracingProducerInterceptor.class.getName());

        kafkaProducer = KafkaProducer.create(vertx.getDelegate(), config);
    }
}
