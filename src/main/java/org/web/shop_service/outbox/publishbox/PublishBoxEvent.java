package org.web.shop_service.outbox.publishbox;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "publish_box")
@Getter
@Setter
public class PublishBoxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "outbox_event_id", nullable = false)
    private UUID outboxEventId;

    @Column(name = "consumer", nullable = false, length = 128)
    private String consumer;
}
