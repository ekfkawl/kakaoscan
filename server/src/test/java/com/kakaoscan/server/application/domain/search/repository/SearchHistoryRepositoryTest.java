package com.kakaoscan.server.application.domain.search.repository;

import com.kakaoscan.server.application.domain.test.TestUserDataInitializer;
import com.kakaoscan.server.domain.point.model.SearchCost;
import com.kakaoscan.server.domain.search.entity.SearchHistory;
import com.kakaoscan.server.domain.search.enums.CostType;
import com.kakaoscan.server.domain.search.repository.SearchHistoryRepository;
import com.kakaoscan.server.domain.user.entity.User;
import com.kakaoscan.server.domain.user.repository.UserRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
class SearchHistoryRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private SearchHistoryRepository searchHistoryRepository;

    private User user;

    private static final String TARGET_PHONE_NUMBER = "01011112222";

    @BeforeEach
    void setUp() {
        user = new TestUserDataInitializer(userRepository).createUser();
    }

    @Test
    @DisplayName("사용자가 target 을 처음 조회하는 경우 -> COST_ORIGIN")
    void testCase1() {
        // given

        // when
        SearchCost searchCost = searchHistoryRepository.getTargetSearchCost(user, TARGET_PHONE_NUMBER);

        // then
        assertEquals(CostType.ORIGIN, searchCost.getCostType());
    }

    @Test
    @DisplayName("이전에 target 을 조회한 적이 있고, 가장 최근에 COST_ORIGIN 포인트가 차감된 시점이 현재 시점으로부터 24시간 이내인 경우 -> COST_DISCOUNT")
    void testCase2() {
        // given
        addSearchHistoryToUser(user, CostType.ORIGIN, LocalDateTime.now().minusHours(3));
        addSearchHistoryToUser(user, CostType.DISCOUNT, LocalDateTime.now().minusHours(2));
        addSearchHistoryToUser(user, CostType.DISCOUNT, LocalDateTime.now().minusHours(1));
        userRepository.save(user);

        // when
        SearchCost searchCost = searchHistoryRepository.getTargetSearchCost(user, TARGET_PHONE_NUMBER);

        // then
        assertEquals(CostType.DISCOUNT, searchCost.getCostType());
    }

    @Test
    @DisplayName("이전에 target 을 조회한 적이 있지만, 가장 최근에 COST_ORIGIN 포인트가 차감된 시점이 현재 시점으로부터 24시간을 초과한 경우 -> COST_ORIGIN")
    void testCase3() {
        // given
        addSearchHistoryToUser(user, CostType.ORIGIN, LocalDateTime.now().minusDays(2));
        addSearchHistoryToUser(user, CostType.DISCOUNT, LocalDateTime.now().minusDays(2).plusHours(1));
        userRepository.save(user);

        // when
        SearchCost searchCost = searchHistoryRepository.getTargetSearchCost(user, TARGET_PHONE_NUMBER);

        // then
        assertEquals(CostType.ORIGIN, searchCost.getCostType());
    }

    @Test
    @DisplayName("이전에 target 을 조회한 적이 있고, 가장 최근에 COST_ORIGIN 포인트가 차감된 시점이 현재 시점으로부터 10분 이내인 경우 -> COST_FREE")
    void testCase4() {
        // given
        addSearchHistoryToUser(user, CostType.ORIGIN, LocalDateTime.now().minusMinutes(9));
        userRepository.save(user);

        // when
        SearchCost searchCost = searchHistoryRepository.getTargetSearchCost(user, TARGET_PHONE_NUMBER);

        // then
        assertEquals(CostType.FREE, searchCost.getCostType());
    }

    @Test
    @DisplayName("이전에 target 을 조회한 적이 있고, 가장 최근에 COST_DISCOUNT 포인트가 차감된 시점이 현재 시점으로부터 10분 이내인 경우 -> COST_FREE")
    void testCase5() {
        // given
        addSearchHistoryToUser(user, CostType.ORIGIN, LocalDateTime.now().minusHours(3));
        addSearchHistoryToUser(user, CostType.DISCOUNT, LocalDateTime.now().minusHours(2));
        addSearchHistoryToUser(user, CostType.DISCOUNT, LocalDateTime.now().minusHours(1).plusMinutes(51));
        userRepository.save(user);

        // when
        SearchCost searchCost = searchHistoryRepository.getTargetSearchCost(user, TARGET_PHONE_NUMBER);

        // then
        assertEquals(CostType.FREE, searchCost.getCostType());
    }
    
    private void addSearchHistoryToUser(User user, CostType costType, LocalDateTime localDateTime) {
        user.addSearchHistory(SearchHistory.builder()
                .targetPhoneNumber(SearchHistoryRepositoryTest.TARGET_PHONE_NUMBER)
                .data("")
                .costType(costType)
                .createdAt(localDateTime)
                .build());
    }

    @TestConfiguration
    static class QuerydslConfig {
        @PersistenceContext
        private EntityManager entityManager;

        @Bean
        public JPAQueryFactory jpaQueryFactory() {
            return new JPAQueryFactory(entityManager);
        }
    }
}