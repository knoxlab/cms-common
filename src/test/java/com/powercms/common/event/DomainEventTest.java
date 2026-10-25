package com.powercms.common.event;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/**
 * DomainEvent 是业务事件发布的契约。所有领域事件必须实现该接口，
 * 业务事务内产生的事件通过事务 Outbox（OutboxService）发布。
 */
class DomainEventTest {

  record SampleEvent(UUID eventId, Instant occurredAt, String eventType, String payload)
      implements DomainEvent {
  }

  @Test
  void sampleEventExposesAllMembers() {
    UUID id = UUID.randomUUID();
    Instant now = Instant.parse("2026-01-01T00:00:00Z");
    SampleEvent event = new SampleEvent(id, now, "content.approved", "{\"id\":42}");

    assertThat(event.eventId()).isEqualTo(id);
    assertThat(event.occurredAt()).isEqualTo(now);
    assertThat(event.eventType()).isEqualTo("content.approved");
    // payload 是示例字段，不属于接口契约本身
    assertThat(event.payload()).isEqualTo("{\"id\":42}");
  }

  @Test
  void eventIdIsRequired() {
    SampleEvent event = new SampleEvent(null, Instant.now(), "noop", null);

    assertThat(event.eventId()).isNull();
    // 接口契约：实现必须暴露 eventId/occurredAt/eventType — 即便实现选择 null
    // 也要由 OutboxService 写入前生成真实 id（由 OutboxEventEntity.create 负责）。
  }
}
