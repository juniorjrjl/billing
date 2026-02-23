package com.algaworks.algashop.billing.infratructure.payment;

import com.algaworks.algashop.billing.domain.model.invoice.payment.Payment;
import com.algaworks.algashop.billing.domain.model.invoice.payment.PaymentGatewayService;
import com.algaworks.algashop.billing.domain.model.invoice.payment.PaymentRequest;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.algaworks.algashop.billing.domain.model.invoice.PaymentMethod.GATEWAY_BALANCE;
import static com.algaworks.algashop.billing.domain.model.invoice.payment.PaymentStatus.PAID;

@Service
public class PaymentGatewayServiceFakeImpl implements PaymentGatewayService {
    @Override
    public Payment capture(final PaymentRequest request) {
        return Payment.builder()
                .invoiceId(request.getInvoiceId())
                .status(PAID)
                .method(request.getPaymentMethod())
                .gatewayCode(UUID.randomUUID().toString())
                .build();
    }

    @Override
    public Payment findByCode(final String gatewayCode) {
        return Payment.builder()
                .invoiceId(UUID.randomUUID())
                .status(PAID)
                .method(GATEWAY_BALANCE)
                .gatewayCode(gatewayCode)
                .build();
    }
}
