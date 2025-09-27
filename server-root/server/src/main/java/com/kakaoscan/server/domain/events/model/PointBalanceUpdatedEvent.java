package com.kakaoscan.server.domain.events.model;

import com.kakaoscan.server.domain.events.model.common.EventMetadata;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PointBalanceUpdatedEvent extends EventMetadata {
    private String email;
}
