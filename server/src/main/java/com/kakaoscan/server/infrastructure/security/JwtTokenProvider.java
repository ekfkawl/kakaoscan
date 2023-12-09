package com.kakaoscan.server.infrastructure.security;

import com.kakaoscan.server.infrastructure.config.JwtProperties;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class JwtTokenProvider {
    private Key key;
    private final JwtProperties jwtProperties;

    @PostConstruct
    protected void init() {
        byte[] keyBytes = Base64.getDecoder().decode(jwtProperties.getSecretKey());
        this.key = new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    public String createAccessToken(Authentication authentication) {
        String username = authentication.getName();
        return createToken(username, jwtProperties.getAccessTokenValidity(), authentication.getAuthorities(), "access");
    }

    public String createRefreshToken(Authentication authentication) {
        String username = authentication.getName();
        return createToken(username, jwtProperties.getRefreshTokenValidity(), Collections.emptyList(), "refresh");
    }

    private String createToken(String subject, long validityDuration, Collection<? extends GrantedAuthority> authorities, String tokenType) {
        Claims claims = Jwts.claims().setSubject(subject);
        claims.put("roles", authorities);
        claims.put("token_type", tokenType);

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
            throw new JwtException("expired or invalid jwt token");
        }
    }

    public boolean validateAccessToken(String token) {
        return validateToken(token, "access");
    }

    public boolean validateRefreshToken(String token) {
        return validateToken(token, "refresh");
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
        List<SimpleGrantedAuthority> authorities = ((List<?>) claims.get("roles")).stream()
                .map(authority -> new SimpleGrantedAuthority(authority.toString()))
                .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(claims.getSubject(), "", authorities);
    }
}
