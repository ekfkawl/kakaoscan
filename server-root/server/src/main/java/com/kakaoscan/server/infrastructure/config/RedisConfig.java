package com.kakaoscan.server.infrastructure.config;

import com.kakaoscan.server.common.utils.ObjectMapperSingleton;
import com.kakaoscan.server.domain.point.model.SearchCost;
import com.kakaoscan.server.domain.search.model.InvalidPhoneNumber;
import com.kakaoscan.server.domain.search.model.NewNumberSearch;
import com.kakaoscan.server.infrastructure.redis.subscriber.DynamicEventReceiver;
import io.ekfkawl.model.EventStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import static com.kakaoscan.server.infrastructure.redis.enums.Topics.*;

@Configuration
public class RedisConfig {
    private static final String DEFAULT_LISTENER_METHOD = "receiveEvent";

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapter, searchTopic());
        container.addMessageListener(listenerAdapter, otherTopic());
        container.addMessageListener(listenerAdapter, eventTraceTopic());
        return container;
    }

    @Bean
    public MessageListenerAdapter listenerAdapter(DynamicEventReceiver receiver) {
        return new MessageListenerAdapter(receiver, DEFAULT_LISTENER_METHOD);
    }

    @Bean
    public PatternTopic searchTopic() {
        return new PatternTopic(SEARCH_EVENT_TOPIC.getTopic());
    }

    @Bean
    public PatternTopic otherTopic() {
        return new PatternTopic(OTHER_EVENT_TOPIC.getTopic());
    }

    @Bean
    public PatternTopic eventTraceTopic() {
        return new PatternTopic(EVENT_TRACE_TOPIC.getTopic());
    }

    @Bean
    public RedisTemplate<String, EventStatus> eventStatusRedisTemplate(RedisConnectionFactory connectionFactory) {
        return createRedisTemplate(connectionFactory, EventStatus.class);
    }

    @Bean
    public RedisTemplate<String, InvalidPhoneNumber> invalidPhoneNumberRedisTemplate(RedisConnectionFactory connectionFactory) {
        return createRedisTemplate(connectionFactory, InvalidPhoneNumber.class);
    }

    @Bean
    public RedisTemplate<String, Integer> integerRedisTemplate(RedisConnectionFactory connectionFactory) {
        return createRedisTemplate(connectionFactory, Integer.class);
    }

    @Bean
    public RedisTemplate<String, SearchCost> searchCostRedisTemplate(RedisConnectionFactory connectionFactory) {
        return createRedisTemplate(connectionFactory, SearchCost.class);
    }

    @Bean
    public RedisTemplate<String, NewNumberSearch> newNumberSearchRedisTemplate(RedisConnectionFactory connectionFactory) {
        return createRedisTemplate(connectionFactory, NewNumberSearch.class);
    }

    private <T> RedisTemplate<String, T> createRedisTemplate(RedisConnectionFactory connectionFactory, Class<T> clazz) {
        RedisTemplate<String, T> template = new RedisTemplate<>();

        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(ObjectMapperSingleton.getInstance(), clazz));
        template.afterPropertiesSet();

        return template;
    }
}
