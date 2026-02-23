package com.algaworks.algashop.billing.domain.model.invoice.payment;

import com.algaworks.algashop.billing.domain.model.FieldValidations;
import com.algaworks.algashop.billing.domain.model.invoice.PaymentMethod;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode
public class Payment {

    private String gatewayCode;
    private UUID invoiceId;
    private PaymentMethod method;
    private PaymentStatus status;

    public Payment(final String gatewayCode,
                   final UUID invoiceId,
                   final PaymentMethod method,
                   final PaymentStatus status) {
        this.gatewayCode = FieldValidations.requireNonBlank(gatewayCode);
        this.invoiceId = invoiceId;
        this.method = method;
        this.status = status;
    }
}
