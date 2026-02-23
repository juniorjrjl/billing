package com.algaworks.algashop.billing.domain.model.invoice;

import com.algaworks.algashop.billing.domain.model.AbstractAuditableAggregateRoot;
import com.algaworks.algashop.billing.domain.model.DomainException;
import com.algaworks.algashop.billing.domain.model.IdGenerator;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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
import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.EnumType.STRING;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Table(name = "INVOICES")
@Entity
@Setter(PRIVATE)
@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PROTECTED)
public class Invoice extends AbstractAuditableAggregateRoot<Invoice> {

    @Id
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
    @Enumerated(STRING)
    private InvoiceStatus invoiceStatus;
    @Nullable
    @OneToOne(cascade = ALL)
    private PaymentSettings paymentSettings;
    @ElementCollection
    @CollectionTable(name = "INVOIVE_LINES_ITEMS", joinColumns = @JoinColumn(name = "invoice_id"))
    private Set<LineItem> items = new HashSet<>();
    @Embedded
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
        final var invoice = new Invoice(
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
        final var event = new InvoiceIssuedEvent(
                invoice.getId(),
                invoice.getCustomerId(),
                invoice.getOrderId(),
                invoice.getIssuedAt()
                );
        invoice.registerEvent(event);
        return invoice;
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
        final var event = new InvoicePaidEvent(
                this.getId(),
                this.getCustomerId(),
                this.getOrderId(),
                requireNonNull(this.getPaidAt())
        );
        registerEvent(event);
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
        final var event = new InvoiceCanceledEvent(
                this.getId(),
                this.getCustomerId(),
                this.getOrderId(),
                requireNonNull(this.getCanceledAt())
        );
        registerEvent(event);
    }

    public void assignPaymentGatewayCode(final String code) {
        if (isNull(this.paymentSettings)){
            final var message = String.format("Invoice %s has no payment settings", this.id);
            throw new DomainException(message);
        }
        if (!isUnpaid()){
            final var message = String.format(
                    "Invoice %s with status '%s' cannot be edited",
                    this.id,
                    this.invoiceStatus
            );
            throw new DomainException(message);
        }
        this.paymentSettings.assignGatewayCode(code);
    }

    public void changePaymentSettings(final PaymentMethod method,
                                      @Nullable
                                      final UUID creditCard) {
        if (!isUnpaid()){
            final var message = String.format(
                    "Invoice %s with status '%s' cannot be edited",
                    this.id,
                    this.invoiceStatus
            );
            throw new DomainException(message);
        }
        this.paymentSettings = PaymentSettings.brandNew(method, creditCard, this);
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
