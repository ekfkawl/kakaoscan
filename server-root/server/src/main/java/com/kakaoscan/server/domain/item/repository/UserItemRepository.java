package com.kakaoscan.server.domain.item.repository;

import com.kakaoscan.server.domain.item.entity.UserItem;
import com.kakaoscan.server.domain.product.enums.ProductType;
import com.kakaoscan.server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserItemRepository extends JpaRepository<UserItem, Long> {
    Optional<UserItem> findByUserAndProductType(User user, ProductType productType);
}
