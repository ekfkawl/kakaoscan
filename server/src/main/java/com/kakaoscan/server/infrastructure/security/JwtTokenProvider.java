package com.kakaoscan.server.infrastructure.security;

import com.kakaoscan.server.infrastructure.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.List;
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
        return createToken(authentication.getName(), jwtProperties.getAccessTokenValidity(), authentication.getAuthorities());
    }

    public String createRefreshToken(Authentication authentication) {
        return createToken(authentication.getName(), jwtProperties.getRefreshTokenValidity(), authentication.getAuthorities());
    }

    private String createToken(String subject, long validityDuration, Collection<? extends GrantedAuthority> authorities) {
        Claims claims = Jwts.claims().setSubject(subject);
        claims.put("roles", authorities);

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityDuration);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(key).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtException("expired or invalid jwt token");
        }
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
        List<SimpleGrantedAuthority> authorities = ((List<?>) claims.get("roles")).stream()
                .map(authority -> new SimpleGrantedAuthority((String) authority))
                .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(claims.getSubject(), "", authorities);
    }
}
