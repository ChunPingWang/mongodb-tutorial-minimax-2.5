package com.example.repository;

import com.example.domain.DomainEvent;
import java.util.List;

public interface EventStore {

    void append(DomainEvent event);

    void appendBatch(List<DomainEvent> events);

    List<DomainEvent> getEventsForAggregate(String aggregateId);

    int getVersion(String aggregateId);

    boolean aggregateExists(String aggregateId);
}
