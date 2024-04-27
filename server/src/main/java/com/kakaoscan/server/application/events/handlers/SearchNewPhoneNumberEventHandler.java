package com.kakaoscan.server.application.events.handlers;

import com.kakaoscan.server.application.service.SearchService;
import com.kakaoscan.server.domain.events.model.SearchNewPhoneNumberEvent;
import com.kakaoscan.server.infrastructure.events.processor.AbstractEventProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

// pub: win32 dll
@Component
@RequiredArgsConstructor
@Log4j2
public class SearchNewPhoneNumberEventHandler extends AbstractEventProcessor<SearchNewPhoneNumberEvent> {
    private final SearchService searchService;

    @Override
    protected void handleEvent(SearchNewPhoneNumberEvent event) {
        try {
            searchService.recordNewPhoneNumber(event.getEmail(), event.getPhoneNumber());
        } catch (DataIntegrityViolationException e) {
            log.info("unique key phone number already recorded: " + event.getPhoneNumber());
        }
    }
}
