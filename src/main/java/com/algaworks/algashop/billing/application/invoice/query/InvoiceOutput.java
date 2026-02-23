package com.algaworks.algashop.billing.application.invoice.query;

import com.algaworks.algashop.billing.application.invoice.managment.PayerData;
import com.algaworks.algashop.billing.domain.model.invoice.InvoiceStatus;
import com.algaworks.algashop.billing.domain.model.invoice.Payer;
import com.algaworks.algashop.billing.domain.model.invoice.PaymentSettings;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceOutput {

    private UUID id;
    private String orderId;
    private UUID customerId;
    private OffsetDateTime issuedAt;
    private OffsetDateTime paidAt;
    private OffsetDateTime canceledAt;
    private OffsetDateTime expiresAt;
    private BigDecimal totalAmount;
    private InvoiceStatus invoiceStatus;
    private PayerData payer;
    private PaymentSettingsOutput paymentSettings;

}
