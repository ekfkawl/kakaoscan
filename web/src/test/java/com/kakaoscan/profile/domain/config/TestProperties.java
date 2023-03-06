package com.kakaoscan.profile.domain.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource({
        "classpath:application-db.properties",
        "classpath:application-dev.properties",
        "classpath:application-smtp.properties",
        "classpath:application-oauth.yml",
        "classpath:application.properties"
})
public class TestProperties {

}
