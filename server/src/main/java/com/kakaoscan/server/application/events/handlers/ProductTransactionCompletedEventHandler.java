package com.kakaoscan.server.application.events.handlers;

import com.kakaoscan.server.application.port.EmailPort;
import com.kakaoscan.server.domain.events.model.ProductTransactionCompletedEvent;
import com.kakaoscan.server.infrastructure.events.processor.AbstractEventProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Log4j2
public class ProductTransactionCompletedEventHandler extends AbstractEventProcessor<ProductTransactionCompletedEvent> {
    private final EmailPort emailPort;

    @Override
    protected void handleEvent(ProductTransactionCompletedEvent event) {
        Map<String, Object> variables = Map.of(
            "productName", event.getTransactionCompletedEmail().getProductName(),
            "domain", event.getTransactionCompletedEmail().getDomain()
        );

        emailPort.send(event.getTransactionCompletedEmail(), variables);

        log.info("send {} transaction completed mail: {}",
                event.getTransactionCompletedEmail().getProductName(),
                event.getTransactionCompletedEmail().getReceiver());
    }
}
