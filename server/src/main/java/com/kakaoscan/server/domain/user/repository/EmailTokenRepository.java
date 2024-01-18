package com.kakaoscan.server.domain.user.repository;

import com.kakaoscan.server.domain.user.model.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailTokenRepository extends JpaRepository<EmailVerificationToken, Long>, EmailTokenRepositoryCustom {
    Optional<EmailVerificationToken> findByToken(String token);
}
