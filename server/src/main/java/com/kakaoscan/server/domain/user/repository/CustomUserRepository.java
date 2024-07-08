package com.kakaoscan.server.domain.user.repository;

import com.kakaoscan.server.domain.user.entity.User;

public interface CustomUserRepository {
    User findByEmailOrThrow(String email);
    User findByIdOrThrow(Long id);
}
