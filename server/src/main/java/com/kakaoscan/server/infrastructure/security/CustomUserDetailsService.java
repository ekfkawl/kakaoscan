package com.kakaoscan.server.infrastructure.security;

import com.kakaoscan.server.application.exception.DeletedUserException;
import com.kakaoscan.server.application.exception.EmailNotVerifiedException;
import com.kakaoscan.server.domain.user.entity.User;
import com.kakaoscan.server.domain.user.model.CustomUserDetails;
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
        User user = userRepository.findByEmailOrThrow(username);

        if (user.isDeleted()) {
            throw new DeletedUserException("deleted user: " + username);
        }

        if (!user.isEmailVerified()) {
            throw new EmailNotVerifiedException("email is not verified for: " + username);
        }

        return new CustomUserDetails(user.getEmail(), user.getPassword(), user.getAuthenticationType(), user.getAuthorities(), null);
    }
}
