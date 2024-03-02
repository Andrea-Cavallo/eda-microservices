package it.eda.shipments.producer.exception;

public class KafkaProducerException extends RuntimeException {



    public KafkaProducerException( Throwable cause) {
        super( cause);
    }
    public KafkaProducerException(String message, Throwable cause) {
        super( message,cause);
    }
}
