package com.kakaoscan.server.infrastructure.events.handlers;

import com.kakaoscan.server.application.port.EventStatusPort;
import com.kakaoscan.server.domain.events.EventStatus;
import com.kakaoscan.server.domain.events.types.external.SetStatusEvent;
import com.kakaoscan.server.infrastructure.events.processor.AbstractEventProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SetStatusEventHandler extends AbstractEventProcessor<SetStatusEvent> {

    private final EventStatusPort eventStatusPort;

    @Override
    protected void handleEvent(SetStatusEvent event) {
        eventStatusPort.setEventStatus(event.getEventId(), new EventStatus(event.getStatus(), event.getMessage()));

        EventStatus eventStatus = eventStatusPort.getEventStatus(event);
        System.out.println(eventStatus.getStatus());
        System.out.println(eventStatus.getMessage());
    }
}