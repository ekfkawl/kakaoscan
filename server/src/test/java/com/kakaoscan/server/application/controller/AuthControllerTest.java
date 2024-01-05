package com.kakaoscan.server.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakaoscan.server.application.controller.api.AuthController;
import com.kakaoscan.server.application.dto.request.LoginRequest;
import com.kakaoscan.server.application.dto.response.LoginResponse;
import com.kakaoscan.server.application.port.AuthPort;
import com.kakaoscan.server.infrastructure.security.JwtTokenUtils;
import jakarta.servlet.http.HttpServletResponse;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerTest {
    private MockMvc mockMvc;

    @Mock
    private AuthPort authPort;

    @Mock
    private JwtTokenUtils jwtTokenUtils;

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
        doNothing().when(jwtTokenUtils).saveRefreshTokenInCookie(anyString(), any(HttpServletResponse.class));

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequest = objectMapper.writeValueAsString(loginRequest);

        // when
        mockMvc.perform(post("/api/login")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
        // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("access-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("refresh-token"));
    }
}
