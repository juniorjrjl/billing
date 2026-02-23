package com.algaworks.algashop.billing.domain.model.invoice.payment;

public interface PaymentGatewayService {

    Payment capture(final PaymentRequest request);

    Payment findByCode(final String gatewayCode);

}
