package com.kakaoscan.server.application.controller;

import com.kakaoscan.server.application.dto.VerificationEmail;
import com.kakaoscan.server.application.port.EmailPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AuthTestController {

    private final EmailPort emailPort;

    @PostMapping("/test")
    public ResponseEntity<?> authenticateUser() {
        emailPort.send(new VerificationEmail("oscgame@naver.com", "ddddd"));
        return ResponseEntity.ok("ㅇㅇㅇ");
    }
}
