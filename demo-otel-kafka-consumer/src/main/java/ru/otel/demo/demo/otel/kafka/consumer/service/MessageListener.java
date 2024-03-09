package ru.otel.demo.demo.otel.kafka.consumer.service;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ru.otel.demo.demo.otel.kafka.consumer.Commit;
import ru.otel.demo.demo.otel.kafka.consumer.CommitRepository;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class MessageListener {

    private static final Logger log = LoggerFactory.getLogger(MessageListener.class);

    private final CommitRepository repository;
    private final ObservationRegistry registry;

    @KafkaListener(topics = "tracing-demo")
    public void listen(@Payload String message, ConsumerRecord<String, String> record) {
        var observation = Observation.createNotStarted("consume-commit-event", registry).start();
        try (var ignored = observation.openScope()) {
            long now = System.currentTimeMillis();
            log.info("Message is '{}'. Received message on topic {} partition {} offset {} header {} key '{}' value '{}'. Time to consume {} ms",
                    record.value(), record.topic(), record.partition(), record.offset(),
                    record.headers(), record.key(), message, now - record.timestamp());
            repository.save(new Commit(new Random().nextLong(), record.value()));
            observation.highCardinalityKeyValue("commit.message", record.value());
            observation.event(Observation.Event.of("event-consumed"));
            log.info("Commit message is '{}'", record.value());
        } finally {
            observation.stop();
        }
    }
}
