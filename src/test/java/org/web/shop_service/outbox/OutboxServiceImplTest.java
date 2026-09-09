package org.web.shop_service.outbox;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxServiceImplTest {
    @Mock
    private OutBoxRepository repository;

    @InjectMocks
    private OutboxServiceImpl service;


    @Test
    void shouldSendEventSuccess() {
        // Given
        final Event event = new Event();
        final String exchange = "exchange";
        final String routingKey = "routing_key";

        // Action
        service.sendEvent(exchange, routingKey, event);

        // Check
        verify(repository, times(1)).save(any());
    }

    @Test
    void getEventsBatch_shouldReturnEvents() {
        // Given
        ReflectionTestUtils.setField(service, "batchSize", 3);
        final var events = List.of(
                outboxEvent(1L),
                outboxEvent(2L),
                outboxEvent(3L)
        );
        when(repository.findAll((Pageable) any())).thenReturn(new PageImpl<>(events));
        // Action
        final List<OutboxEvent> actualEvents = service.getEventsBatch();
        // Check
        assertThat(actualEvents).hasSize(events.size());
        verify(repository, times(1)).findAll((Pageable) any());
    }

    @Test
    void shouldDeleteEventsSuccess() {
        // Given
        final var events = List.of(
                outboxEvent(1L),
                outboxEvent(2L),
                outboxEvent(3L)
        );
        // Action
        service.deleteEvents(events);
        // Check
        verify(repository, times(1)).deleteAllInBatch(any());
    }

    private static OutboxEvent outboxEvent(Long id) {
        final var event = new OutboxEvent();
        event.setId(id);
        event.setExchange("exchange");
        event.setRoutingKey("routing_key");
        event.setPayload("payload");
        return event;
    }
}
