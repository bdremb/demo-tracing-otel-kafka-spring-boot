package ru.otel.demo.demo.otel.kafka.producer.service;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FetchCommitService {

    private final RestTemplate restTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObservationRegistry registry;

    FetchCommitService(RestTemplate restTemplate, KafkaTemplate<String, String> kafkaTemplate, ObservationRegistry registry) {
        this.restTemplate = restTemplate;
        this.kafkaTemplate = kafkaTemplate;
        this.registry = registry;
    }

    public String fetchCommit() {
        var observation = Observation.createNotStarted("fetch-commit", registry).start();
        try (var ignored = observation.openScope()) {
            String commitMsg = restTemplate.getForObject("https://whatthecommit.com/index.txt", String.class);
            //noinspection DataFlowIssue
            observation.highCardinalityKeyValue("commit.message", commitMsg);
            observation.event(Observation.Event.of("commit-fetched"));
            this.kafkaTemplate.send("tracing-demo", "commit-msg", commitMsg);
            final Observation.Event event = Observation.Event.of("event-triggered");
            observation.event(event);
            return commitMsg;
        } catch (Exception ex) {
            observation.error(ex);
            throw ex;
        } finally {
            observation.stop();
        }
    }
}