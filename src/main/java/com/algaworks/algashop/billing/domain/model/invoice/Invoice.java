package com.algaworks.algashop.billing.domain.model.invoice;

import com.algaworks.algashop.billing.domain.model.DomainException;
import com.algaworks.algashop.billing.domain.model.IdGenerator;
import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static com.algaworks.algashop.billing.domain.model.invoice.InvoiceStatus.CANCELED;
import static com.algaworks.algashop.billing.domain.model.invoice.InvoiceStatus.PAID;
import static com.algaworks.algashop.billing.domain.model.invoice.InvoiceStatus.UNPAID;
import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Setter(PRIVATE)
@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PROTECTED)
public class Invoice {

    private UUID id;
    private String orderId;
    private UUID customerId;
    private OffsetDateTime issuedAt;
    @Nullable
    private OffsetDateTime paidAt;
    @Nullable
    private OffsetDateTime canceledAt;
    private OffsetDateTime expiresAt;
    private BigDecimal totalAmount;
    private InvoiceStatus invoiceStatus;
    @Nullable
    private PaymentSettings paymentSettings;
    private Set<LineItem> items = new HashSet<>();
    private Payer payer;
    @Nullable
    private String cancelReason;

    public static Invoice issue(final String orderId,
                                final UUID customerId,
                                final Payer payer,
                                final Set<LineItem> items) {
        if (StringUtils.isBlank(orderId)) {
            throw new IllegalArgumentException("Order ID cannot be blank");
        }
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Items cannot be empty");
        }
        return new  Invoice(
                IdGenerator.generateTimeBasedUUID(),
                orderId,
                customerId,
                OffsetDateTime.now(),
                null,
                null,
                OffsetDateTime.now().plusDays(3),
                items.stream().map(LineItem::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add),
                UNPAID,
                null,
                items,
                payer,
                null
        );
    }

    public void markAsPaid() {
        if (!isUnpaid()){
            final var message = String.format(
                    "Invoice %s with status '%s' cannot be marked as paid",
                    this.id,
                    this.invoiceStatus
            );
            throw new DomainException(message);
        }
        setPaidAt(OffsetDateTime.now());
        setInvoiceStatus(PAID);
    }

    public void cancel(final String cancelReason) {
        if (isCanceled()){
            final var message = String.format(
                    "Invoice %s is already cancelled",
                    this.id
            );
            throw new DomainException(message);
        }
        setCancelReason(cancelReason);
        setCanceledAt(OffsetDateTime.now());
        setInvoiceStatus(CANCELED);
    }

    public void assignPaymentGatewayCode(final String code) {
        if (isNull(this.paymentSettings)){
            final var message = String.format("Invoice %s has no payment settings", this.id);
            throw new DomainException(message);
        }
        if (!isPaid()){
            final var message = String.format(
                    "Invoice %s with status '%s' cannot be edited",
                    this.id,
                    this.invoiceStatus
            );
            throw new DomainException(message);
        }
        this.paymentSettings.assignGatewayCode(code);
    }

    public void changePaymentSettings(final PaymentMethod method, final UUID creditCard) {
        if (!isPaid()){
            final var message = String.format(
                    "Invoice %s with status '%s' cannot be edited",
                    this.id,
                    this.invoiceStatus
            );
            throw new DomainException(message);
        }
        this.paymentSettings = PaymentSettings.brandNew(method, creditCard);
    }

    public Set<LineItem> getItems() {
        return Collections.unmodifiableSet(items);
    }

    public boolean isCanceled() {
        return this.invoiceStatus == CANCELED;
    }

    public boolean isUnpaid() {
        return this.invoiceStatus == UNPAID;
    }

    public boolean isPaid() {
        return this.invoiceStatus == PAID;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Invoice invoice)) return false;
        return Objects.equals(id, invoice.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
