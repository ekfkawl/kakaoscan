package com.kakaoscan.server.domain.events;

import com.kakaoscan.server.domain.events.enums.EventStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventStatus implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private EventStatusEnum status;
    private String message;

    public EventStatus(EventStatusEnum status) {
        this.status = status;
        this.message = null;
    }
}
