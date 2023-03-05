package com.kakaoscan.profile.domain.controller;

import com.kakaoscan.profile.global.oauth.OAuthAttributes;
import com.kakaoscan.profile.global.oauth.annotation.UserAttributes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice("com.kakaoscan.profile.domain.controller")
public class ViewControllerAdvice {
    @Value("${websocket.server}")
    private String server;

    @Value("${link.github}")
    private String lGithub;

    @Value("${link.sns}")
    private String lSns;

    @Value("${link.blog}")
    private String lBlog;

    @ModelAttribute
    public void addAttrSignIn(Model model, @UserAttributes OAuthAttributes attributes) {

        model.addAttribute("user", attributes);
        model.addAttribute("tick", System.currentTimeMillis());
        model.addAttribute("server", server);
        model.addAttribute("lGithub", lGithub);
        model.addAttribute("lSns", lSns);
        model.addAttribute("lBlog", lBlog);

    }
}
