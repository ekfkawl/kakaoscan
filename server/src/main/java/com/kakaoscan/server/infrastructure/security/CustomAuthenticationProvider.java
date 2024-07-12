package com.kakaoscan.server.infrastructure.security;

import com.kakaoscan.server.application.dto.response.UserItem;
import com.kakaoscan.server.application.exception.DeletedUserException;
import com.kakaoscan.server.application.exception.EmailNotVerifiedException;
import com.kakaoscan.server.common.utils.PasswordEncoderSingleton;
import com.kakaoscan.server.domain.product.enums.ProductType;
import com.kakaoscan.server.domain.user.entity.User;
import com.kakaoscan.server.domain.user.model.CustomUserDetails;
import com.kakaoscan.server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {
    private final UserRepository userRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        User user = userRepository.findByEmailOrThrow(username);

        if (!PasswordEncoderSingleton.getInstance().matches(password, user.getPassword())) {
            throw new BadCredentialsException("invalid password");
        }

        if (user.isDeleted()) {
            throw new DeletedUserException("deleted user: " + username);
        }

        if (!user.isEmailVerified()) {
            throw new EmailNotVerifiedException("email is not verified for: " + username);
        }

        List<UserItem> userItems = user.getItems().stream()
                .filter(userItem -> ProductType.SNAPSHOT_PRESERVATION.equals(userItem.getProductType()) && userItem.getExpiredAt().isAfter(LocalDateTime.now()))
                .map(userItem -> new UserItem(userItem.getProductType(), userItem.getProductType().getDisplayName(), userItem.getExpiredAt()))
                .toList();

        CustomUserDetails userDetails = new CustomUserDetails(user.getId(), user.getEmail(), user.getPassword(), userItems, user.getAuthenticationType(), user.getAuthorities(), null);

        return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
