package ru.otel.demo.demo.otel.kafka.consumer;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommitRepository extends CrudRepository<Commit, Long> {
}
