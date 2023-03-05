package com.kakaoscan.profile.domain.controller.api;

import com.kakaoscan.profile.domain.dto.UserRequestUnlockDTO;
import com.kakaoscan.profile.domain.service.UserRequestUnlockService;
import com.kakaoscan.profile.global.oauth.OAuthAttributes;
import com.kakaoscan.profile.global.oauth.annotation.UserAttributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@Log4j2
public class UserRequestUnlockController extends ApiBaseController {

    private final UserRequestUnlockService userRequestUnlockService;

    @PostMapping("/req-unlock")
    @PreAuthorize("hasRole('ROLE_GUEST')")
    public ResponseEntity<?> updateUnlockMessage(@Valid @RequestBody UserRequestUnlockDTO userRequestUnlockDTO,
                                                 @UserAttributes OAuthAttributes attributes) {

        userRequestUnlockService.updateUnlockMessage(userRequestUnlockDTO.toEntity(attributes.getEmail()));

        return ResponseEntity.ok().build();
    }
}
