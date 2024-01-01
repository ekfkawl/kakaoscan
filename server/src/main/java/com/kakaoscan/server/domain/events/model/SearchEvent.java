package com.kakaoscan.server.domain.events.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SearchEvent extends EventMetadata {
    private String email;
    private String phoneNumber;

    @Builder
    public SearchEvent(String eventId, String email, String phoneNumber) {
        super();
        this.eventId = eventId;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
}
