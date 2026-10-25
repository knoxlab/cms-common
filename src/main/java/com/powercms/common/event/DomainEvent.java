package com.powercms.common.event;

import java.time.Instant;
import java.util.UUID;

/**
 * 领域事件标记接口。
 * 业务事务内产生的事件必须通过事务 Outbox 发布（规格书 9.2），消费者以 eventId 幂等去重。
 */
public interface DomainEvent {

    UUID eventId();

    Instant occurredAt();

    /** 事件类型，例如 content.approved、release.created。 */
    String eventType();
}
