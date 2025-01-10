package com.kakaoscan.server.infrastructure.config;

import com.kakaoscan.server.application.exception.TransactionIllegalStateException;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Random;
import java.util.Set;

@Data
@Configuration
@ConfigurationProperties(prefix = "words")
public class WordProperties {
    private List<String> adjectives;
    private List<String> nouns;

    public String combination() {
        Random random = new Random();
        List<String> adjectives = this.adjectives;
        List<String> nouns = this.nouns;
        String adjective = adjectives.get(random.nextInt(adjectives.size()));
        String noun = nouns.get(random.nextInt(nouns.size()));
        return adjective + noun;
    }

    public String generateUniqueDepositor(Set<String> existingDepositors) {
        String combination;

        int attempts = 0;
        final int maxAttempts = 255;
        do {
            combination = this.combination();
            if (++attempts > maxAttempts) {
                throw new TransactionIllegalStateException("현재 결제 신청이 불가합니다.");
            }
        } while (existingDepositors.contains(combination));

        return combination;
    }
}
