package com.algaworks.algashop.billing.application.invoice.managment;

import com.algaworks.algashop.billing.domain.model.DomainException;
import com.algaworks.algashop.billing.domain.model.creditcard.CreditCardNotFoundException;
import com.algaworks.algashop.billing.domain.model.creditcard.CreditCardRepository;
import com.algaworks.algashop.billing.domain.model.invoice.InvoiceIssuedEvent;
import com.algaworks.algashop.billing.domain.model.invoice.InvoicePaidEvent;
import com.algaworks.algashop.billing.domain.model.invoice.InvoiceRepository;
import com.algaworks.algashop.billing.domain.model.invoice.InvoicingService;
import com.algaworks.algashop.billing.domain.model.invoice.PaymentMethod;
import com.algaworks.algashop.billing.domain.model.invoice.payment.PaymentGatewayService;
import com.algaworks.algashop.billing.domain.model.invoice.payment.PaymentRequest;
import com.algaworks.algashop.billing.infratructure.listener.InvoiceEventListener;
import com.algaworks.algashop.billing.utility.AbstractApplicationTest;
import com.algaworks.algashop.billing.utility.InvoiceDataBuilder;
import com.algaworks.algashop.billing.utility.databuilder.application.GenerateInvoiceInputDataBuilder;
import com.algaworks.algashop.billing.utility.databuilder.domain.CreditCardDataBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.algaworks.algashop.billing.domain.model.invoice.PaymentMethod.CREDIT_CARD;
import static com.algaworks.algashop.billing.domain.model.invoice.PaymentMethod.GATEWAY_BALANCE;
import static com.algaworks.algashop.billing.domain.model.invoice.payment.PaymentStatus.PAID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
class InvoiceManagementApplicationServiceTest extends AbstractApplicationTest {

    private final InvoiceManagementApplicationService applicationService;
    private final InvoiceRepository invoiceRepository;
    private final CreditCardRepository creditCardRepository;

    @MockitoSpyBean
    private InvoicingService invoicingService;

    @MockitoBean
    private PaymentGatewayService paymentGatewayService;

    @MockitoSpyBean
    private InvoiceEventListener eventListener;

    @Autowired
    public InvoiceManagementApplicationServiceTest(final JdbcTemplate jdbcTemplate,
                                                   final InvoiceManagementApplicationService applicationService,
                                                   final InvoiceRepository invoiceRepository,
                                                   final CreditCardRepository creditCardRepository) {
        super(jdbcTemplate);
        this.applicationService = applicationService;
        this.invoiceRepository = invoiceRepository;
        this.creditCardRepository = creditCardRepository;
    }

    @Test
    void givenPaymentWithCreditCardWhenGenerateThenReturnInvoiceId() {
        final var creditCard = CreditCardDataBuilder.builder().build();
        final var paymentSettings = customFaker.invoiceInput().paymentSettings()
                .toBuilder()
                .creditCardId(creditCard.getId())
                .method(CREDIT_CARD)
                .build();
        creditCardRepository.save(creditCard);
        final var input = GenerateInvoiceInputDataBuilder.builder()
                .withCustomerId(creditCard::getCustomerId)
                .withPaymentSettings(() ->  paymentSettings)
                .build();
        final var actual = applicationService.generate(input);
        assertThat(invoiceRepository.existsById(actual)).isTrue();
        final var actualInvoice = invoiceRepository.findById(actual).orElseThrow();
        assertThat(actualInvoice.getVersion()).isZero();
        assertThat(actualInvoice.getCreatedAt()).isNotNull();
        assertThat(actualInvoice.getCreatedByUserId()).isNotNull();
        verify(eventListener).listen(any(InvoiceIssuedEvent.class));
    }

    @Test
    void givenPaymentWithoutCustomerIdAndNoCreditCardWhenGenerateThenReturnInvoiceId() {
        final var paymentSettings = customFaker.invoiceInput().paymentSettings()
                .toBuilder()
                .method(GATEWAY_BALANCE)
                .creditCardId(null)
                .build();
        final var input = GenerateInvoiceInputDataBuilder.builder()
                .withPaymentSettings(() ->  paymentSettings)
                .build();
        final var actual = applicationService.generate(input);
        assertThat(invoiceRepository.existsById(actual)).isTrue();
    }

    @Test
    void givenNonStoredCreditCardWhenGenerateThenThrowException() {
        final var paymentSettings = customFaker.invoiceInput().paymentSettings()
                .toBuilder()
                .method(CREDIT_CARD)
                .build();
        final var input = GenerateInvoiceInputDataBuilder.builder()
                .withPaymentSettings(() ->  paymentSettings)
                .build();

        assertThatExceptionOfType(CreditCardNotFoundException.class)
                .isThrownBy(() -> applicationService.generate(input));
        verifyNoInteractions(invoicingService);
    }

    @Test
    void givenExistingInvoiceWhenGenerateThenReturnThrowException() {
        final var paymentSettings = customFaker.invoiceInput().paymentSettings()
                .toBuilder()
                .method(GATEWAY_BALANCE)
                .creditCardId(null)
                .build();
        final var input = GenerateInvoiceInputDataBuilder.builder()
                .withPaymentSettings(() ->  paymentSettings)
                .build();
        applicationService.generate(input);
        assertThatExceptionOfType(DomainException.class)
                .isThrownBy(() -> applicationService.generate(input));
    }

    @Test
    void shouldProcessInvoicePayment(){
        final var invoice = InvoiceDataBuilder.builder().buildIssue();
        invoice.changePaymentSettings(customFaker.option(PaymentMethod.class), UUID.randomUUID());
        final var payment = customFaker.invoiceInput()
                .payment()
                .toBuilder()
                .status(PAID)
                .invoiceId(invoice.getId())
                .method(invoice.getPaymentSettings().getPaymentMethod())
                .build();
        invoiceRepository.save(invoice);
        when(paymentGatewayService.capture(any(PaymentRequest.class))).thenReturn(payment);
        applicationService.processPayment(invoice.getId());
        final var actual = invoiceRepository.findById(invoice.getId()).orElseThrow();
        assertThat(actual.isPaid()).isTrue();
        verify(eventListener).listen(any(InvoicePaidEvent.class));
    }

}