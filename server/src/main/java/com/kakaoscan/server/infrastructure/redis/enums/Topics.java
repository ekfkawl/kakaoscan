package com.kakaoscan.server.infrastructure.redis.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Topics {
    SEARCH_EVENT_TOPIC("searchTopic"),
    OTHER_EVENT_TOPIC("otherTopic");

    private final String topic;
}
