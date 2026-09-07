package org.web.shop_service.outbox;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;

@Getter
@RequiredArgsConstructor
public class Event {
    private final ZonedDateTime createdAt;
}
