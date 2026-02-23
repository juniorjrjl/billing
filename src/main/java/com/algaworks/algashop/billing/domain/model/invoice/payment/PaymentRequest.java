package com.algaworks.algashop.billing.domain.model.invoice.payment;

import com.algaworks.algashop.billing.domain.model.invoice.Payer;
import com.algaworks.algashop.billing.domain.model.invoice.PaymentMethod;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.util.UUID;

import static com.algaworks.algashop.billing.domain.model.invoice.PaymentMethod.CREDIT_CARD;
import static java.util.Objects.requireNonNull;

@Getter
@EqualsAndHashCode
@Builder
public class PaymentRequest {

    private PaymentMethod paymentMethod;
    private BigDecimal amount;
    private UUID invoiceId;
    @Nullable
    private UUID creditCardId;
    private Payer payer;

    public PaymentRequest(final PaymentMethod paymentMethod,
                          final BigDecimal amount,
                          final UUID invoiceId,
                          @Nullable
                          final UUID creditCardId,
                          final Payer payer) {
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.invoiceId = invoiceId;
        this.creditCardId = paymentMethod == CREDIT_CARD ?
                requireNonNull(creditCardId) :
                creditCardId;
        this.payer = payer;
    }
}
