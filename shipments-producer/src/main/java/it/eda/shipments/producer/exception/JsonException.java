package it.eda.shipments.producer.exception;

public class JsonException extends  RuntimeException {
    public JsonException(String message, Throwable cause) {
        super(message, cause);
    }
}
