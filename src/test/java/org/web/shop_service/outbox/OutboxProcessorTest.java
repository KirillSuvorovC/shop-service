package org.web.shop_service.outbox;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OutboxProcessorTest {
    public static final String ROUTING_KEY = "routingKey";
    public static final String EXCHANGE = "exchange";
    @Mock
    private RabbitTemplate template;
    @Mock
    private OutboxService service;
    @InjectMocks
    private OutboxProcessor processor;

    @Captor
    private ArgumentCaptor<String> exchangeCaptor;
    @Captor
    private ArgumentCaptor<String> routingKeyCaptor;
    @Captor
    private ArgumentCaptor<Message> messageCaptor;

    @Test
    void sendEventsInRabbitMQ_shouldSendSuccess() {
        // Given
        final String payload = "{\"eventId\": \"eventId\"}";
        final var outboxEvent = new OutboxEvent();
        outboxEvent.setExchange(EXCHANGE);
        outboxEvent.setRoutingKey(ROUTING_KEY);
        outboxEvent.setPayload(payload);
        when(service.getEventsBatch()).thenReturn(List.of(outboxEvent));
        // Action
        processor.sendEventsInRabbitMQ();
        // Check
        verify(service, times(1)).getEventsBatch();
        verify(service, times(1)).deleteEvents(any());
        verify(template, times(1)).send(exchangeCaptor.capture(),
                                                              routingKeyCaptor.capture(),
                                                              messageCaptor.capture());

        final var message = messageCaptor.getValue();
        final String actualPayload = new String(message.getBody(), StandardCharsets.UTF_8);
        final String contentType = message.getMessageProperties().getContentType();
        assertThat(actualPayload).isEqualTo(payload);
        assertThat(contentType).isEqualTo("application/json");
        assertThat(exchangeCaptor.getValue()).isEqualTo(EXCHANGE);
        assertThat(routingKeyCaptor.getValue()).isEqualTo(ROUTING_KEY);
    }

    @Test
    void sendEventsInRabbitMQ_whenEventsNotFound_doNothing() {
        // Given
        when(service.getEventsBatch()).thenReturn(new ArrayList<>());
        // Action && Check
        processor.sendEventsInRabbitMQ();
        verify(service, times(1)).getEventsBatch();
        verify(template, times(0)).convertAndSend(any(), any(), (Message) any());
        verify(service, times(0)).deleteEvents(any());
    }
}
