package it.eda.shipments.producer.conf;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapSetter;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Map;

public class TracingProducerInterceptor<K, V> implements ProducerInterceptor<K, V> {
    private static final TextMapSetter<ProducerRecord<?, ?>> SETTER = (record, key, value) -> {
        if (record.headers() != null) {
            record.headers().add(key, value.getBytes());
        }
    };

    @Override
    public ProducerRecord<K, V> onSend(ProducerRecord<K, V> record) {
        GlobalOpenTelemetry.getPropagators().getTextMapPropagator().inject(Context.current(), record, SETTER);
        return record;
    }

    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
    }

    @Override
    public void close() {
    }

    @Override
    public void configure(Map<String, ?> configs) {
    }
}
