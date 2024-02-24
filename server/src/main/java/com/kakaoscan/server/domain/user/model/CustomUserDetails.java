package com.kakaoscan.server.domain.user.model;

import com.kakaoscan.server.application.dto.response.UserData;
import com.kakaoscan.server.domain.user.enums.Role;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Getter
public class CustomUserDetails implements UserDetails {
    private final String email;
    private final String password;
    private final Collection<GrantedAuthority> authorities;
    private final Map<String, Object> attributes;

    public CustomUserDetails(String email, String password, Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes) {
        this.email = email;
        this.password = password;
        this.authorities = new ArrayList<>(authorities);
        this.attributes = attributes;
    }

    public UserData convertToUserData() {
        String authority = this.authorities.stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElseThrow(() -> new IllegalStateException("no authorities"));

        Role role = Role.fromAuthority(authority);

        return new UserData(this.email, role, this.getImageUrl());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getImageUrl() {
        return this.attributes != null ? (String) this.attributes.get("picture") : null;
    }
}
