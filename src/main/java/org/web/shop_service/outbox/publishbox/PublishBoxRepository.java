package org.web.shop_service.outbox.publishbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PublishBoxRepository extends JpaRepository<PublishBoxEvent, Long> {
    boolean existsByOutboxEventIdAndConsumer(UUID outboxEventId, String consumer);
}
