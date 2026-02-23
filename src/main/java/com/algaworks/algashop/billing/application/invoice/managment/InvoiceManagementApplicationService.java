package com.algaworks.algashop.billing.application.invoice.managment;

import com.algaworks.algashop.billing.domain.model.creditcard.CreditCardNotFoundException;
import com.algaworks.algashop.billing.domain.model.creditcard.CreditCardRepository;
import com.algaworks.algashop.billing.domain.model.invoice.InvoiceNotFoundException;
import com.algaworks.algashop.billing.domain.model.invoice.InvoiceRepository;
import com.algaworks.algashop.billing.domain.model.invoice.InvoicingService;
import com.algaworks.algashop.billing.domain.model.invoice.payment.Payment;
import com.algaworks.algashop.billing.domain.model.invoice.payment.PaymentGatewayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceManagementApplicationService {

    private final PaymentGatewayService paymentGatewayService;
    private final InvoicingService service;
    private final InvoiceRepository repository;
    private final CreditCardRepository creditCardRepository;
    private final InvoiceInputDisassembler disassembler;

    @Transactional
    public UUID generate(final GenerateInvoiceInput input){
        verifyCreditCard(input.getPaymentSettings().getCreditCardId(), input.getCustomerId());

        final var payer = disassembler.toDomain(input.getPayer());
        final var items = disassembler.toDomain(input.getItems());

        final var domain = service.issue(input.getOrderId(), input.getCustomerId(), payer, items);
        final var paymentSettingsInput = input.getPaymentSettings();
        domain.changePaymentSettings(paymentSettingsInput.getMethod(), paymentSettingsInput.getCreditCardId());
        repository.saveAndFlush(domain);
        return domain.getId();
    }

    @Transactional
    public void processPayment(final UUID invoiceId){
        final var domain = repository.findById(invoiceId).orElseThrow(InvoiceNotFoundException::new);
        final var paymentRequest = disassembler.toCaptureRequest(domain);
        Payment payment;
        try {
            payment = paymentGatewayService.capture(paymentRequest);
        }catch (Exception e){
            final var message = "Payment capture failed";
            log.error(message, e);
            domain.cancel(message);
            return;
        }
        service.assignPayment(domain, payment);
        repository.saveAndFlush(domain);
    }

    private void verifyCreditCard(@Nullable final UUID creditCardId, final UUID customerId) {
        if (nonNull(creditCardId) && !creditCardRepository.existsByIdAndCustomerId(creditCardId, customerId)) {
            throw new CreditCardNotFoundException();
        }
    }

}
