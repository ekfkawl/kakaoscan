package com.kakaoscan.server.infrastructure.security;

import com.kakaoscan.server.application.exception.EmailNotVerifiedException;
import com.kakaoscan.server.domain.user.model.CustomUserDetails;
import com.kakaoscan.server.domain.user.model.User;
import com.kakaoscan.server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("user not found with email: " + username));

        if (!user.isEmailVerified()) {
            throw new EmailNotVerifiedException("email is not verified for: " + username);
        }

        return new CustomUserDetails(user.getEmail(), user.getPassword(), user.getAuthorities(), null);
    }
}
