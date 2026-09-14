package org.web.shop_service.outbox.publishbox;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.web.shop_service.outbox.Event;
import org.web.shop_service.outbox.publishbox.exceptions.PublishBoxException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PublishBoxServiceImplTest {
    private static final String CONSUMER = "consumer";

    @Mock
    private PublishBoxRepository repository;

    @InjectMocks
    private PublishBoxServiceImpl service;

    @Test
    @DisplayName("Если событие существует - вернуть true")
    void existsEvent_shouldReturnTrue_whenEventExists() {
        // Given
        final UUID eventId = UUID.randomUUID();
        when(repository.existsByOutboxEventIdAndConsumer(eventId, CONSUMER))
                .thenReturn(true);

        // Action
        final boolean exists = service.existsEvent(eventId, CONSUMER);

        // Check
        assertThat(exists).isTrue();
        verify(repository, times(1))
                .existsByOutboxEventIdAndConsumer(eventId, CONSUMER);
    }

    @Test
    @DisplayName("Если событие не существует - вернуть false")
    void existsEvent_shouldReturnFalse_whenEventDoesNotExist() {
        // Given
        final UUID eventId = UUID.randomUUID();
        when(repository.existsByOutboxEventIdAndConsumer(eventId, CONSUMER))
                .thenReturn(false);

        // Action
        final boolean exists = service.existsEvent(eventId, CONSUMER);

        // Check
        assertThat(exists).isFalse();
        verify(repository, times(1))
                .existsByOutboxEventIdAndConsumer(eventId, CONSUMER);
    }

    @Test
    @DisplayName("Событие должно сохраниться, если его нету в базе данных")
    void publishEvent_shouldSaveEvent_whenNotExists() {
        // Given
        final var event = new Event();
        final UUID eventId = event.getEventId();
        when(repository.existsByOutboxEventIdAndConsumer(eventId, CONSUMER))
                .thenReturn(false);

        // Action
        service.publishEvent(event, CONSUMER);

        // Check
        final var captor = ArgumentCaptor.forClass(PublishBoxEvent.class);
        verify(repository, times(1)).save(captor.capture());

        final var savedEvent = captor.getValue();
        assertThat(savedEvent.getOutboxEventId()).isEqualTo(eventId);
        assertThat(savedEvent.getConsumer()).isEqualTo(CONSUMER);
    }

    @Test
    @DisplayName("Ничего не делаем, если событие уже существует")
    void publishEvent_shouldDoNothing_whenEventAlreadyExists() {
        // Given
        final var event = new Event();
        final UUID eventId = event.getEventId();
        when(repository.existsByOutboxEventIdAndConsumer(eventId, CONSUMER))
                .thenReturn(true);

        // Action
        service.publishEvent(event, CONSUMER);

        // Check
        verify(repository, times(0)).save(any());
    }

    @Test
    @DisplayName("""
            Бросаем исключение, если пошло что-то не так
            при сохранении события
            """)
    void publishEvent_shouldThrowException_whenSaveFails() {
        // Given
        final var event = new Event();
        final UUID eventId = event.getEventId();
        when(repository.existsByOutboxEventIdAndConsumer(eventId, CONSUMER))
                .thenReturn(false);

        when(repository.save(any())).thenThrow(new RuntimeException("DB error"));

        // Action && Check
        assertThatThrownBy(() -> service.publishEvent(event, CONSUMER))
                .isInstanceOf(PublishBoxException.class);

        verify(repository, times(1)).save(any());
    }
}
