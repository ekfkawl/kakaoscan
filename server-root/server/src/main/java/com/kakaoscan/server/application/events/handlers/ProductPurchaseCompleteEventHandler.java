package com.kakaoscan.server.application.events.handlers;

import com.kakaoscan.server.application.port.EmailPort;
import com.kakaoscan.server.domain.events.model.ProductPurchaseCompleteEvent;
import com.kakaoscan.server.infrastructure.events.processor.AbstractEventProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Log4j2
public class ProductPurchaseCompleteEventHandler extends AbstractEventProcessor<ProductPurchaseCompleteEvent> {
    private final EmailPort emailPort;

    @Override
    protected void handleEvent(ProductPurchaseCompleteEvent event) {
        Map<String, Object> variables = Map.of(
            "productName", event.getPurchaseCompleteEmail().getProductName(),
            "domain", event.getPurchaseCompleteEmail().getDomain()
        );

        emailPort.send(event.getPurchaseCompleteEmail(), variables);

        log.info("send {} transaction completed mail: {}",
                event.getPurchaseCompleteEmail().getProductName(),
                event.getPurchaseCompleteEmail().getReceiver());
    }
}
