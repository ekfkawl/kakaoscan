package io.ekfkawl.model;

import java.time.LocalDateTime;

public interface EventBase {
    String getEventId();
    LocalDateTime getCreatedAt();
}
