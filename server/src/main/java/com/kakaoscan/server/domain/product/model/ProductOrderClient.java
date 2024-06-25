package com.kakaoscan.server.domain.product.model;

import com.kakaoscan.server.application.dto.request.WebhookProductOrderRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "productOrder", url = "${pay.base-url}")
public interface ProductOrderClient {
    @PostMapping("/order")
    void createProductOrder(@RequestBody WebhookProductOrderRequest order);

    @PostMapping("/order-exclude")
    void excludeProductOrder(@RequestBody WebhookProductOrderRequest order);
}
