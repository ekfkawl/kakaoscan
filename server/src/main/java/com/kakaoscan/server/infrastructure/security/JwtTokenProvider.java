package com.kakaoscan.server.infrastructure.security;

import com.kakaoscan.server.domain.user.model.CustomUserDetails;
import com.kakaoscan.server.infrastructure.config.JwtProperties;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.*;

import static com.kakaoscan.server.common.utils.ExceptionHandler.handleException;

@RequiredArgsConstructor
@Component
public class JwtTokenProvider {
    private Key key;
    private final JwtProperties jwtProperties;
    private final UserDetailsService userDetailsService;

    @PostConstruct
    protected void init() {
        byte[] keyBytes = Base64.getDecoder().decode(jwtProperties.getSecretKey());
        this.key = new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    public String createAccessToken(Authentication authentication) {
        String username = authentication.getName();
        return createToken(username, jwtProperties.getAccessTokenValidity(), authentication.getAuthorities(), "access", ((CustomUserDetails) authentication.getPrincipal()).getAttributes());
    }

    public String createRefreshToken(Authentication authentication) {
        String username = authentication.getName();
        return createToken(username, jwtProperties.getRefreshTokenValidity(), Collections.emptyList(), "refresh", ((CustomUserDetails) authentication.getPrincipal()).getAttributes());
    }

    private String createToken(String subject, long validityDuration, Collection<? extends GrantedAuthority> authorities, String tokenType, Map<String, Object> attributes) {
        Claims claims = Jwts.claims().setSubject(subject);
        claims.put("roles", authorities);
        claims.put("token_type", tokenType);
        claims.put("attributes", attributes);

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityDuration);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

    private boolean validateToken(String token, String expectedTokenType) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token);
            String tokenType = claims.getBody().get("token_type", String.class);
            return expectedTokenType.equals(tokenType);
        } catch (JwtException | IllegalArgumentException e) {
            handleException("expired or invalid jwt token", e);
            return false;
        }
    }

    public boolean validateAccessToken(String token) {
        return validateToken(token, "access");
    }

    public boolean validateRefreshToken(String token) {
        return validateToken(token, "refresh");
    }

    @SuppressWarnings("unchecked")
    public Authentication getAuthentication(String token) {
        try {
            Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
            String username = claims.getSubject();
            Map<String, Object> attributes = claims.get("attributes", HashMap.class);

            CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(username);
            CustomUserDetails customUserDetails = new CustomUserDetails(userDetails.getId(), userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthenticationType(), userDetails.getAuthorities(), attributes);

            return new UsernamePasswordAuthenticationToken(customUserDetails, null, userDetails.getAuthorities());
        } catch (JwtException e) {
            handleException("failed to authenticate using the provided refresh token", e);
            return null;
        }
    }

}
