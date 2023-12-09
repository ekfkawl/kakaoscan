package com.kakaoscan.server.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakaoscan.server.application.dto.LoginRequest;
import com.kakaoscan.server.application.dto.LoginResponse;
import com.kakaoscan.server.application.port.AuthPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class AuthControllerTest {
    private MockMvc mockMvc;

    @Mock
    private AuthPort authPort;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    @DisplayName("유효한 로그인이면 jwt 토큰을 반환한다")
    public void validLoginReturnsJwtTokens() throws Exception {
        // given
        LoginRequest loginRequest = new LoginRequest("test@test.com", "1234");
        LoginResponse loginResponse = new LoginResponse("access-token", "refresh-token");

        given(authPort.authenticate(any(LoginRequest.class))).willReturn(loginResponse);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequest = objectMapper.writeValueAsString(loginRequest);

        // when
        mockMvc.perform(post("/login")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
        // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));
    }
}
