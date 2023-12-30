package com.kakaoscan.server.domain.events.types.external;

import com.kakaoscan.server.domain.events.EventMetadata;
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
