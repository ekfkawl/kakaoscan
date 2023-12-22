package com.kakaoscan.server.infrastructure.events.handlers;

import com.kakaoscan.server.domain.events.types.external.SearchEvent;
import com.kakaoscan.server.infrastructure.events.processor.AbstractEventProcessor;

public class SearchEventHandler extends AbstractEventProcessor<SearchEvent> {

    @Override
    protected void handleEvent(SearchEvent event) {
        System.out.println("-----------SearchEvent----------------");
    }
}