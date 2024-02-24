package com.kakaoscan.server.domain.user.model.oauth2;

import com.kakaoscan.server.domain.user.model.CustomUserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class GoogleOAuth2User implements OAuth2UserCommon, OAuth2User {
    private final Map<String, Object> attributes;
    private final Collection<GrantedAuthority> authorities;

    public GoogleOAuth2User(Map<String, Object> attributes, Collection<? extends GrantedAuthority> authorities) {
        this.attributes = attributes;
        this.authorities = new ArrayList<>(authorities);
    }

    public CustomUserDetails convertToCustomUserDetails() {
        return new CustomUserDetails(this.getEmail(), null, this.getAuthorities(), this.attributes);
    }

    @Override
    public String getId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getName() {
        return this.getEmail();
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getImageUrl() {
        return (String) attributes.get("picture");
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
