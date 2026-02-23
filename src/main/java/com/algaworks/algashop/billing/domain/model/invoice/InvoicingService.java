package com.algaworks.algashop.billing.domain.model.invoice;

import com.algaworks.algashop.billing.domain.model.DomainException;
import com.algaworks.algashop.billing.domain.model.invoice.payment.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvoicingService {

    private final InvoiceRepository repository;

    public Invoice issue(final String orderId, final UUID customerId, final Payer payer, final Set<LineItem> lineItems) {
        if (repository.existsByOrderId(orderId)){
            final var message = String.format("Invoice already exists for order %s", orderId);
            throw new DomainException(message);
        }

        return Invoice.issue(orderId, customerId, payer, lineItems);
    }

    public void assignPayment(final Invoice invoice, final Payment payment) {
        invoice.assignPaymentGatewayCode(payment.getGatewayCode());
        switch (payment.getStatus()) {
            case FAILED -> invoice.cancel("Payment failed");
            case REFUNDED -> invoice.cancel("Payment refunded");
            case PAID -> invoice.markAsPaid();
        }
    }

}
