package com.kakaoscan.profile.domain.controller;

import com.kakaoscan.profile.domain.model.UseCount;
import com.kakaoscan.profile.domain.service.AccessLimitService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * view
 */
@RestController
@RequiredArgsConstructor
@PropertySource("classpath:application-link.properties")
public class ViewController {

    private final AccessLimitService accessLimitService;

    @Value("${kakaoscan.all.date.maxcount}")
    private long allLimitCount;

    @Value("${kakaoscan.server.count}")
    private long serverCount;

    @GetMapping("/")
    public ModelAndView index(@RequestParam(required = false, defaultValue = "") String phoneNumber) {
        ModelAndView mv = new ModelAndView("index");

        UseCount useCount = accessLimitService.getUseCount();
        long remainingCount = (allLimitCount * serverCount) - useCount.getTotalCount();
        if (remainingCount < 0) {
            remainingCount = 0;
        }

        mv.addObject("todayRemainingCount", String.format("%d/%d", remainingCount, allLimitCount * serverCount));
        mv.addObject("phoneNumber", phoneNumber);

        return mv;
    }

    @GetMapping("/req-unlock")
    public ModelAndView unlock() {
        ModelAndView mv = new ModelAndView("unlock");

        return mv;
    }

}
