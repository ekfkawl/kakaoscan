package com.kakaoscan.server.infrastructure.adapter;

import com.kakaoscan.server.application.domain.test.TestUserDataInitializer;
import com.kakaoscan.server.application.port.PointPort;
import com.kakaoscan.server.domain.user.entity.User;
import com.kakaoscan.server.domain.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static com.kakaoscan.server.infrastructure.config.TestConstant.TEST_USER_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class PointAdapterTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PointPort pointPort;

    private User user;

    private static final int THREAD_COUNT = 10;

    @BeforeEach
    @DisplayName("사용자 및 포인트 초기 설정")
    void setUp() {
        user = new TestUserDataInitializer(userRepository).createUser();
        System.out.println("----------- Init Balance : " + user.getPoint().getBalance());
    }

    @AfterEach
    @DisplayName("최종 포인트 잔액")
    void getPointBalance() {
        int balance = user.getPoint().getBalance();
        System.out.println("----------- Last Balance : " + balance);
    }

    @Test
    @DisplayName("동시에 포인트 차감을 시도하는 경우, 단 한 번만 진행 되어야한다")
    void testConcurrentPointDeduction() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        AtomicInteger successfulDeductions = new AtomicInteger();

        for (int i = 0; i < THREAD_COUNT; i++) {
            executor.submit(() -> {
                try {
                    boolean result = pointPort.deductPoints(TEST_USER_ID, 1000);
                    if (result) {
                        successfulDeductions.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        // 모든 쓰레드가 작업을 완료할 때까지 대기
        latch.await();

        assertEquals(1, successfulDeductions.get());

        executor.shutdown();
    }

}