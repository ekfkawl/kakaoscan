package com.kakaoscan.server.infrastructure.email.types;

import com.kakaoscan.server.infrastructure.email.template.EmailTemplate;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductTransactionCompletedEmail extends EmailTemplate {
    private String productName;
    private String domain;

    public ProductTransactionCompletedEmail(String receiver, String productName, String domain) {
        super(receiver, String.format("[카카오스캔] %s 결제가 완료되었습니다.", productName), "transaction_completed_email");
        this.productName = productName;
        this.domain = domain;
    }
}
