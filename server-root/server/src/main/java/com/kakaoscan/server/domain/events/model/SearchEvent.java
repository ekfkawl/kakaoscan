package com.kakaoscan.server.domain.events.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kakaoscan.server.domain.events.model.common.EventMetadata;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SearchEvent extends EventMetadata {
    private String email;
    @Setter
    private String phoneNumber;
    @JsonProperty("isId")
    private boolean isId;

    @Builder
    public SearchEvent(String eventId, String email, String phoneNumber, boolean isId) {
        super();
        this.eventId = eventId;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.isId = isId;
    }
}
