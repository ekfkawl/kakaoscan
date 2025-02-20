package com.kakaoscan.server.domain.product.model;

import com.kakaoscan.server.application.dto.request.WebhookProductOrderRequest;
import feign.RequestInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "productOrder", url = "${pay.base-url}", configuration = ProductOrderClient.FeignConfig.class)
public interface ProductOrderClient {
    @PostMapping("/order")
    void createProductOrder(@RequestBody WebhookProductOrderRequest order);

    @PostMapping("/order-exclude")
    void excludeProductOrder(@RequestBody WebhookProductOrderRequest order);

    @Configuration
    public static class FeignConfig {
        @Bean
        public RequestInterceptor requestInterceptor() {
            return requestTemplate -> {
                requestTemplate.header("Content-Type", "application/json");
                requestTemplate.header("x-api-key", System.getenv("PAY_API_KEY"));
                requestTemplate.header("x-mall-id", System.getenv("PAY_MALL_ID"));
            };
        }
    }
}
