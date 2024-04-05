package com.kakaoscan.server.application.events.handlers;

import com.kakaoscan.server.application.service.SearchService;
import com.kakaoscan.server.domain.events.model.SearchNewPhoneNumberEvent;
import com.kakaoscan.server.domain.search.entity.NewPhoneNumber;
import com.kakaoscan.server.domain.search.model.NewNumberSearch;
import com.kakaoscan.server.domain.search.repository.NewPhoneNumberRepository;
import com.kakaoscan.server.domain.user.entity.User;
import com.kakaoscan.server.domain.user.repository.UserRepository;
import com.kakaoscan.server.infrastructure.events.processor.AbstractEventProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

// pub: win32 dll
@Component
@RequiredArgsConstructor
@Log4j2
public class SearchNewPhoneNumberEventHandler extends AbstractEventProcessor<SearchNewPhoneNumberEvent> {
    private final UserRepository userRepository;
    private final SearchService searchService;
    private final NewPhoneNumberRepository newPhoneNumberRepository;

    @Override
    protected void handleEvent(SearchNewPhoneNumberEvent event) {
        try {
            User user = userRepository.findByEmailOrThrow(event.getEmail());

            searchService.recordNewPhoneNumber(user, event.getPhoneNumber());

            List<NewPhoneNumber> newPhoneNumbers = newPhoneNumberRepository.findNewPhoneNumbersByDate(user, LocalDate.now());
            NewNumberSearch newNumberSearch = new NewNumberSearch(newPhoneNumbers);

            searchService.cacheNewNumberSearch(event.getEmail(), newNumberSearch);

            log.info(event.getEmail() + " - newNumberSearchCount: " + newNumberSearch.getCount());
        } catch (DataIntegrityViolationException e) {
            log.info("unique key phone number already recorded: " + event.getPhoneNumber());
        }
    }
}
