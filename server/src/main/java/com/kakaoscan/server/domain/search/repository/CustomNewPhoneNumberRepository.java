package com.kakaoscan.server.domain.search.repository;

import com.kakaoscan.server.domain.search.entity.NewPhoneNumber;
import com.kakaoscan.server.domain.user.entity.User;

import java.time.LocalDate;
import java.util.List;

public interface CustomNewPhoneNumberRepository {
    List<NewPhoneNumber> findNewPhoneNumbersByDate(User user, LocalDate localDate);
}
