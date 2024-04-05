package com.kakaoscan.server.domain.search.repository;

import com.kakaoscan.server.domain.search.entity.NewPhoneNumber;
import com.kakaoscan.server.domain.search.entity.QNewPhoneNumber;
import com.kakaoscan.server.domain.user.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class CustomNewPhoneNumberRepositoryImpl implements CustomNewPhoneNumberRepository {
    private final JPAQueryFactory factory;

    @Override
    public List<NewPhoneNumber> findNewPhoneNumbersByDate(User user, LocalDate localDate) {
        QNewPhoneNumber newPhoneNumber = QNewPhoneNumber.newPhoneNumber;

        LocalDateTime startOfDay = localDate.atStartOfDay();
        LocalDateTime endOfDay = localDate.atTime(23, 59, 59);

        return factory
                .selectFrom(newPhoneNumber)
                .where(newPhoneNumber.user.eq(user)
                        .and(newPhoneNumber.createdAt.between(startOfDay, endOfDay)))
                .fetch();
    }
}
