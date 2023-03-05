package com.kakaoscan.profile.global.security.service;

import com.kakaoscan.profile.domain.entity.User;
import com.kakaoscan.profile.domain.repository.UserRepository;
import com.kakaoscan.profile.global.oauth.OAuthAttributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        log.info("call UserDetails loadUserByUsername");

        User user = userRepository.findById(email)
                .orElseThrow(() -> new UsernameNotFoundException("email not found"));

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        String[] role = user.getRole().getKey().split(",");
        for (String s : role) {
            grantedAuthorities.add(new SimpleGrantedAuthority(s));
        }

        return OAuthAttributes.builder()
                .email(user.getEmail())
                .authorities(grantedAuthorities)
                .build();
    }
}
