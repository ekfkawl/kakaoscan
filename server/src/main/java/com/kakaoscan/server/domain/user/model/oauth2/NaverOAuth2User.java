package com.kakaoscan.server.domain.user.model.oauth2;

import com.kakaoscan.server.domain.user.enums.AuthenticationType;
import com.kakaoscan.server.domain.user.model.CustomUserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class NaverOAuth2User implements OAuth2UserCommon, OAuth2User {
    private final Map<String, Object> attributes;
    private final Collection<GrantedAuthority> authorities;

    public NaverOAuth2User(Map<String, Object> attributes, Collection<? extends GrantedAuthority> authorities) {
        this.attributes = attributes;
        this.authorities = new ArrayList<>(authorities);
    }

    public CustomUserDetails convertToCustomUserDetails() {
        return new CustomUserDetails(null, this.getEmail(), null, null, AuthenticationType.NAVER, this.getAuthorities(), this.attributes);
    }

    @Override
    public Long getId() {
        return (Long) attributes.get("id");
    }

    @Override
    public String getName() {
        return (String) attributes.get("nickname");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("id");
    }

    @Override
    public String getImageUrl() {
        return (String) attributes.get("profile_image");
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }
}
