package org.web.shop_service.outbox;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OutboxProcessorTest {
    @Mock
    private RabbitTemplate template;
    @Mock
    private OutboxService service;
    @InjectMocks
    private OutboxProcessor processor;

    @Test
    void sendEventsInRabbitMQ_shouldSendSuccess() {
        // Given
        when(service.getEventsBatch()).thenReturn(List.of(new OutboxEvent()));
        // Action && Check
        processor.sendEventsInRabbitMQ();
        verify(service, times(1)).getEventsBatch();
        verify(template, times(1)).convertAndSend(any(), any(), (Event)any());
        verify(service, times(1)).deleteEvents(any());
    }

    @Test
    void sendEventsInRabbitMQ_whenEventsNotFound_doNothing() {
        // Given
        when(service.getEventsBatch()).thenReturn(new ArrayList<>());
        // Action && Check
        processor.sendEventsInRabbitMQ();
        verify(service, times(1)).getEventsBatch();
        verify(template, times(0)).convertAndSend(any(), any(), (Event)any());
        verify(service, times(0)).deleteEvents(any());
    }
}
