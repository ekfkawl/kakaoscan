package com.kakaoscan.server.domain.user.repository;

import com.kakaoscan.server.domain.user.entity.QUser;
import com.kakaoscan.server.domain.user.entity.User;
import com.kakaoscan.server.infrastructure.exception.UserNotFoundException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomUserRepositoryImpl implements CustomUserRepository {
    private final JPAQueryFactory factory;

    @Override
    public User findByEmailOrThrow(String email) {
        QUser user = QUser.user;
        User result = factory.selectFrom(user)
                .where(user.email.eq(email))
                .fetchOne();

        if (result == null) {
            throw new UserNotFoundException("user email not found: " + email);
        }

        return result;
    }
}
