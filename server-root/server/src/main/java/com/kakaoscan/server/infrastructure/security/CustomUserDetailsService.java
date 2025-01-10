package com.kakaoscan.server.infrastructure.security;

import com.kakaoscan.server.application.dto.response.UserItem;
import com.kakaoscan.server.domain.product.enums.ProductType;
import com.kakaoscan.server.domain.user.entity.User;
import com.kakaoscan.server.domain.user.model.CustomUserDetails;
import com.kakaoscan.server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmailOrThrow(username);

        List<UserItem> userItems = user.getItems().stream()
                .filter(userItem -> ProductType.SNAPSHOT_PRESERVATION.equals(userItem.getProductType()) && userItem.getExpiredAt().isAfter(LocalDateTime.now()))
                .map(userItem -> new UserItem(userItem.getProductType(), userItem.getProductType().getDisplayName(), userItem.getExpiredAt()))
                .toList();

        return new CustomUserDetails(user.getId(), user.getEmail(), user.getPassword(), userItems, user.getAuthenticationType(), user.getAuthorities(), null);
    }
}
