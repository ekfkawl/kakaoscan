package com.kakaoscan.server.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Random;

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
}
