package com.kakaoscan.server.application.infrastructure.redis;

import com.kakaoscan.server.application.domain.TestEvent;
import com.kakaoscan.server.application.infrastructure.events.processor.TestEventProcessor;
import com.kakaoscan.server.infrastructure.events.processor.EventProcessor;
import com.kakaoscan.server.infrastructure.events.processor.EventProcessorFactory;
import com.kakaoscan.server.infrastructure.redis.publisher.EventPublisher;
import com.kakaoscan.server.infrastructure.redis.subscriber.DynamicEventReceiver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(SpringExtension.class)
public class EventPubSubTest {
    private static final Logger logger = LoggerFactory.getLogger(EventPubSubTest.class);

    @MockBean
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private EventProcessor testEventProcessor;

    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private EventProcessorFactory eventProcessorFactory;

    @Autowired
    private DynamicEventReceiver eventReceiver;

    private static final int THREAD_POOL_COUNT = 2;

    @TestConfiguration
    static class EventPubSubTestConfig {
        @Bean
        public EventProcessor eventProcessor() {
            return new TestEventProcessor();
        }

        @Bean
        public EventPublisher eventPublisher(StringRedisTemplate stringRedisTemplate) {
            return new EventPublisher(stringRedisTemplate);
        }

        @Bean
        public EventProcessorFactory eventProcessorFactory() {
            return new EventProcessorFactory();
        }

        @Bean
        public ExecutorService taskExecutorService() {
            return Executors.newFixedThreadPool(THREAD_POOL_COUNT);
        }

        @Bean
        public DynamicEventReceiver dynamicEventReceiver(EventProcessorFactory eventProcessorFactory) {
            return new DynamicEventReceiver(eventProcessorFactory, taskExecutorService());
        }
    }

    @BeforeEach
    public void setup() {
        eventProcessorFactory.registerProcessor("TestEvent", testEventProcessor);
    }

    @Test
    @DisplayName("이벤트를 발행하고 구독은 쓰레드풀로 병렬처리 한다")
    public void testEventPublishAndReceive() throws InterruptedException {
        // given
        TestEvent testEvent = new TestEvent();
        String eventData1 = "{\"eventId\":\"1\",\"createdAt\":\"2024-01-01T01:00:00\"}";
        String eventData2 = "{\"eventId\":\"2\",\"createdAt\":\"2024-01-01T02:00:00\"}";

        logger.info(() -> "Thread Pool Count: " + THREAD_POOL_COUNT);

        // pub
        Mockito.when(stringRedisTemplate.convertAndSend(eq("testTopic"), anyString())).thenReturn(1L);

        // when
        eventPublisher.publish("testTopic", testEvent);

        eventReceiver.processEvent("TestEvent", eventData1);
        eventReceiver.processEvent("TestEvent", eventData2);
        // 병렬 처리 확인
        Thread.sleep(2000L);

        // then
        Mockito.verify(stringRedisTemplate).convertAndSend(eq("testTopic"), anyString());
        assertTrue(((TestEventProcessor) testEventProcessor).isProcessCalled());
    }
}
