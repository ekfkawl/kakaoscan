package com.kakaoscan.profile.domain.client.controller.api;

import com.kakaoscan.profile.domain.service.AccessLimitService;
import com.kakaoscan.profile.domain.validator.annotation.CheckKey;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.PropertySource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Validated
@PropertySource("classpath:application-key.properties")
public class AccessLimitController extends ApiBaseController {

    private final AccessLimitService accessLimitService;

    @PostMapping("/limit")
    public boolean updateUseCount(@RequestParam Integer serverIndex, @RequestParam @CheckKey String key) {

        return accessLimitService.updateUseCount(serverIndex);
    }
}
