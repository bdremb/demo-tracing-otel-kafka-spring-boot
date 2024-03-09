package ru.otel.demo.demo.otel.kafka.producer.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otel.demo.demo.otel.kafka.producer.service.FetchCommitService;

@RestController
public class HomeController {

    private final FetchCommitService service;

    public HomeController(FetchCommitService service) {
        this.service = service;
    }

    @RequestMapping("/")
    String service1() {
        return service.fetchCommit();
    }
}
