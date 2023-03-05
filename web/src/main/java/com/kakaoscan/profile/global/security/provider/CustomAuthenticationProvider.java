package com.kakaoscan.profile.global.security.provider;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;

    public CustomAuthenticationProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        log.info("call authenticate");

        String email = authentication.getName();
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());

        } catch (UsernameNotFoundException e) {
            throw new BadCredentialsException("invalid user");
        }
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return false;
    }
}
