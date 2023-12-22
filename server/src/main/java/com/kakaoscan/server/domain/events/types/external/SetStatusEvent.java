package com.kakaoscan.server.domain.events.types.external;

import com.kakaoscan.server.domain.events.EventMetadata;
import com.kakaoscan.server.domain.events.enums.EventStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SetStatusEvent extends EventMetadata {
    private EventStatusEnum status;
    private String message;
}
