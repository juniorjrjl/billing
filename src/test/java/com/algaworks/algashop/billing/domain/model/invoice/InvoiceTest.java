package com.algaworks.algashop.billing.domain.model.invoice;

import com.algaworks.algashop.billing.domain.model.DomainException;
import com.algaworks.algashop.billing.utility.CustomFaker;
import com.algaworks.algashop.billing.utility.InvoiceDataBuilder;
import com.algaworks.algashop.billing.utility.tag.UnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.FieldSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.algaworks.algashop.billing.domain.model.invoice.InvoiceStatus.UNPAID;
import static java.time.temporal.ChronoUnit.MILLIS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.within;

@UnitTest
class InvoiceTest {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    @BeforeEach
    void setup(){
        CustomFaker.getInstance().reseed();
    }


    private static final List<Arguments> givenInvalidParamsWhenIssueInvoiceThenThrowException =
            List.of(
                    Arguments.of(
                            "",
                            UUID.randomUUID(),
                            customFaker.invoice().payer(),
                            Stream.generate(() -> customFaker.invoice().lineItem())
                                    .limit(customFaker.number().numberBetween(1, 5))
                                    .collect(Collectors.toSet())
                            ),
                    Arguments.of(
                            "  ",
                            UUID.randomUUID(),
                            customFaker.invoice().payer(),
                            Stream.generate(() -> customFaker.invoice().lineItem())
                                    .limit(customFaker.number().numberBetween(1, 5))
                                    .collect(Collectors.toSet())
                    ),
                    Arguments.of(
                            customFaker.lorem().word(),
                            UUID.randomUUID(),
                            customFaker.invoice().payer(),
                            Collections.<LineItem>emptySet()
                    )
            );

    @ParameterizedTest
    @FieldSource
    void givenInvalidParamsWhenIssueInvoiceThenThrowException(final String orderId,
                                                              final UUID customerId,
                                                              final Payer payer,
                                                              final Set<LineItem> items){
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> Invoice.issue(orderId, customerId, payer, items));
    }

    @Test
    void shouldIssueInvoice(){
        final var invoice = InvoiceDataBuilder.builder().buildIssue();
        assertThat(invoice).hasNoNullFieldsOrPropertiesExcept("paidAt",
                "canceledAt",
                "paymentSettings",
                "cancelReason",
                "createdByUserId",
                "createdAt",
                "lastModifiedByUserId",
                "lastModifiedAt"
        );
        assertThat(invoice.getInvoiceStatus()).isEqualTo(UNPAID);
        assertThat(invoice.getIssuedAt()).isBeforeOrEqualTo(OffsetDateTime.now());
        assertThat(invoice.getExpiresAt()).isCloseTo(invoice.getIssuedAt().plusDays(3), within(1, MILLIS));
    }

    @Test
    void shouldMarkAsPaidInvoice(){
        final var invoice = InvoiceDataBuilder.builder().buildIssue();
        invoice.markAsPaid();
        assertThat(invoice.getPaidAt()).isBeforeOrEqualTo(OffsetDateTime.now());
        assertThat(invoice.isPaid()).isTrue();
    }

    private static Stream<Invoice> givenNonUnpaidInvoiceWhenMarkAsPaidThenThrowException(){
        final var paid = InvoiceDataBuilder.builder().buildIssue();
        paid.markAsPaid();
        final var canceled = InvoiceDataBuilder.builder().buildIssue();
        canceled.cancel(customFaker.chuckNorris().fact());
        return Stream.of(
                paid,
                canceled
        );
    }

    @ParameterizedTest
    @MethodSource
    void givenNonUnpaidInvoiceWhenMarkAsPaidThenThrowException(final Invoice  invoice){
        assertThatExceptionOfType(DomainException.class)
                .isThrownBy(invoice::markAsPaid);
    }

    @Test
    void shouldMarkAsCanceled(){
        final var invoice = InvoiceDataBuilder.builder().buildIssue();
        final var cancelReason = customFaker.chuckNorris().fact();
        invoice.cancel(cancelReason);
        assertThat(invoice.getCanceledAt()).isBeforeOrEqualTo(OffsetDateTime.now());
        assertThat(invoice.isCanceled()).isTrue();
        assertThat(invoice.getCancelReason()).isEqualTo(cancelReason);
    }

    @Test
    void givenCanceledInvoiceWhenMarkAsCanceledThenThrowException(){
        final var invoice = InvoiceDataBuilder.builder().buildIssue();
        invoice.cancel(customFaker.chuckNorris().fact());
        assertThatExceptionOfType(DomainException.class)
                .isThrownBy(() -> invoice.cancel(customFaker.chuckNorris().fact()));
    }

    private static Stream<Invoice> givenNonPaidInvoiceWhenChangePaymentSettingsThenThrowException(){
        final var paid = InvoiceDataBuilder.builder().buildIssue();
        paid.markAsPaid();
        final var canceled = InvoiceDataBuilder.builder().buildIssue();
        canceled.cancel(customFaker.chuckNorris().fact());
        return Stream.of(
                paid,
                canceled
        );
    }

    @Test
    void shouldChangePaymentSettings(){
        final var invoice = InvoiceDataBuilder.builder().buildIssue();
        final var paymentMethod = customFaker.option(PaymentMethod.class);
        final var creditCard = UUID.randomUUID();
        invoice.changePaymentSettings(paymentMethod, creditCard);
        assertThat(invoice.getPaymentSettings()).isNotNull();
        final var paymentSettings = invoice.getPaymentSettings();
        assertThat(paymentSettings.getId()).isNotNull();
        assertThat(paymentSettings.getCreditCardId()).isEqualTo(creditCard);
        assertThat(paymentSettings.getPaymentMethod()).isEqualTo(paymentMethod);
        assertThat(paymentSettings.getGatewayCode()).isNull();
    }

    @ParameterizedTest
    @MethodSource
    void givenNonPaidInvoiceWhenChangePaymentSettingsThenThrowException(final Invoice invoice){
        assertThatExceptionOfType(DomainException.class)
                .isThrownBy(() -> invoice.changePaymentSettings(
                        customFaker.option(PaymentMethod.class),
                        UUID.randomUUID()
                        )
                );
    }

    private static Stream<Arguments> givenNonPaidInvoiceWhenAssignPaymentGatewayCodeThenThrowException(){
        final var unpaid = InvoiceDataBuilder.builder().buildIssue();
        final var canceled = InvoiceDataBuilder.builder().buildIssue();
        canceled.changePaymentSettings(
                customFaker.option(PaymentMethod.class),
                UUID.randomUUID()
        );
        canceled.markAsPaid();
        canceled.cancel(customFaker.chuckNorris().fact());
        final var alreadyAssigned = InvoiceDataBuilder.builder().buildIssue();
        alreadyAssigned.changePaymentSettings(customFaker.option(PaymentMethod.class), UUID.randomUUID());
        alreadyAssigned.assignPaymentGatewayCode(customFaker.number().digits(4));

        final var invalidCode = InvoiceDataBuilder.builder().buildIssue();
        invalidCode.changePaymentSettings(
                customFaker.option(PaymentMethod.class),
                UUID.randomUUID()
        );
        return Stream.of(
                Arguments.of(unpaid, customFaker.number().digits(4), DomainException.class),
                Arguments.of(canceled, customFaker.number().digits(4), DomainException.class),
                Arguments.of(alreadyAssigned, customFaker.number().digits(4), IllegalArgumentException.class),
                Arguments.of(invalidCode, "", IllegalArgumentException.class),
                Arguments.of(invalidCode, "  ", IllegalArgumentException.class)
        );
    }

    @ParameterizedTest
    @MethodSource
    void givenNonPaidInvoiceWhenAssignPaymentGatewayCodeThenThrowException(final Invoice invoice,
                                                                           final String code,
                                                                           final Class<? extends RuntimeException> expectedException){
        assertThatExceptionOfType(expectedException)
                .isThrownBy(() -> invoice.assignPaymentGatewayCode(code)
                );
    }

    @Test
    void shouldAssignPaymentGatewayCode(){
        final var invoice = InvoiceDataBuilder.builder().buildIssue();
        invoice.changePaymentSettings(customFaker.option(PaymentMethod.class), UUID.randomUUID());
        final var code = customFaker.number().digits(4);
        invoice.assignPaymentGatewayCode(code);
        assertThat(invoice.getPaymentSettings()).isNotNull();
        assertThat(invoice.getPaymentSettings().getGatewayCode()).isEqualTo(code);
    }

}