package com.kakaoscan.server.domain.user.model;

import com.kakaoscan.server.application.dto.response.UserData;
import com.kakaoscan.server.application.dto.response.UserItem;
import com.kakaoscan.server.domain.user.enums.AuthenticationType;
import com.kakaoscan.server.domain.user.enums.Role;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Getter
public class CustomUserDetails implements UserDetails {
    private final Long id;
    private final String email;
    private final String password;
    private final List<UserItem> items;
    private final AuthenticationType authenticationType;
    private final Collection<GrantedAuthority> authorities;
    private final Map<String, Object> attributes;

    public CustomUserDetails(Long id, String email, String password, List<UserItem> items, AuthenticationType authenticationType, Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.items = items;
        this.authenticationType = authenticationType;
        this.authorities = new ArrayList<>(authorities);
        this.attributes = attributes;
    }

    public UserData convertToUserData() {
        String authority = this.authorities.stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElseThrow(() -> new IllegalStateException("no authorities"));

        Role role = Role.fromAuthority(authority);

        return new UserData(this.email, role, this.getItems(), this.getImageUrl(), this.getNickName(), this.authenticationType);
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
        return Optional.ofNullable(this.attributes)
                .map(attr -> (String) attr.get("picture"))
                .orElseGet(() -> Optional.ofNullable(this.attributes)
                        .map(attr -> (String) attr.get("profile_image"))
                        .orElse(null)
                );
    }

    public String getNickName() {
        return Optional.ofNullable(this.attributes)
                .map(attr -> (String) attr.get("name"))
                .orElseGet(() -> Optional.ofNullable(this.attributes)
                        .map(attr -> (String) attr.get("nickname"))
                        .orElse(null)
                );
    }
}
