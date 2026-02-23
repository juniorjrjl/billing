package com.algaworks.algashop.billing.utility.databuilder.application;

import com.algaworks.algashop.billing.application.invoice.managment.GenerateInvoiceInput;
import com.algaworks.algashop.billing.application.invoice.managment.LineItemInput;
import com.algaworks.algashop.billing.application.invoice.managment.PayerData;
import com.algaworks.algashop.billing.application.invoice.managment.PaymentSettingsInput;
import com.algaworks.algashop.billing.utility.CustomFaker;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class GenerateInvoiceInputDataBuilder {

    private final CustomFaker customFaker = CustomFaker.getInstance();

    @With
    private Supplier<String> orderId = () -> UUID.randomUUID().toString();
    @With
    private Supplier<UUID> customerId = UUID::randomUUID;
    @With
    private Supplier<PaymentSettingsInput> paymentSettings = () ->customFaker.invoiceInput().paymentSettings();
    @With
    private Supplier<PayerData> payer = () -> customFaker.invoiceInput().payer();
    @With
    private Supplier<Set<LineItemInput>> items = () -> Stream.generate(() -> customFaker.invoiceInput().lineItem())
            .limit(customFaker.number().numberBetween(5, 10)).collect(Collectors.toSet());

    public static GenerateInvoiceInputDataBuilder builder() {
        return new GenerateInvoiceInputDataBuilder();
    }

    public GenerateInvoiceInput build() {
        return GenerateInvoiceInput.builder()
                .orderId(orderId.get())
                .customerId(customerId.get())
                .paymentSettings(paymentSettings.get())
                .payer(payer.get())
                .items(items.get())
                .build();
    }

}
