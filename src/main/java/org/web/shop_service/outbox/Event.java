package org.web.shop_service.outbox;

import lombok.Getter;

import java.util.UUID;

@Getter
public class Event {
    private final UUID eventId;

    public Event() {
        this.eventId = UUID.randomUUID();
    }
}
