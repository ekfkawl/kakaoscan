package com.kakaoscan.profile.domain.client.controller;

import com.kakaoscan.profile.domain.model.UseCount;
import com.kakaoscan.profile.domain.service.AccessLimitService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * view
 */
@RestController
@RequiredArgsConstructor
@PropertySource("classpath:application-link.properties")
public class IndexController {

    private final AccessLimitService accessLimitService;

    @Value("${websocket.server}")
    private String server;

    @Value("${link.github}")
    private String lGithub;

    @Value("${link.sns}")
    private String lSns;

    @Value("${link.blog}")
    private String lBlog;

    @Value("${kakaoscan.all.date.maxcount}")
    private long allLimitCount;

    @Value("${kakaoscan.server.count}")
    private long serverCount;

    @GetMapping("/")
    public ModelAndView index(@RequestParam(required = false, defaultValue = "") String phoneNumber, HttpServletRequest request) {
        ModelAndView mv = new ModelAndView("index");

        mv.addObject("tick", System.currentTimeMillis());
        mv.addObject("server", server);
        mv.addObject("lGithub", lGithub);
        mv.addObject("lSns", lSns);
        mv.addObject("lBlog", lBlog);

        UseCount useCount = accessLimitService.getUseCount();
        long remainingCount = (allLimitCount * serverCount) - useCount.getTotalCount();
        if (remainingCount < 0) {
            remainingCount = 0;
        }
        mv.addObject("todayRemainingCount", String.format("%d/%d", remainingCount, allLimitCount * serverCount));

        mv.addObject("phoneNumber", phoneNumber);

        return mv;
    }

}
