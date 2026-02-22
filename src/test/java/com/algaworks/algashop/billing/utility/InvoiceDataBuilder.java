package com.algaworks.algashop.billing.utility;

import com.algaworks.algashop.billing.domain.model.invoice.Invoice;
import com.algaworks.algashop.billing.domain.model.invoice.LineItem;
import com.algaworks.algashop.billing.domain.model.invoice.Payer;
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
public class InvoiceDataBuilder {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    @With
    private Supplier<String> orderId = () -> UUID.randomUUID().toString();
    @With
    private Supplier<UUID> customerId = UUID::randomUUID;
    @With
    private Supplier<Payer> payer = () -> customFaker.invoice().payer();
    @With
    private Supplier<Set<LineItem>> items = () -> Stream.generate(
            () -> customFaker.invoice().lineItem()
    ).limit(customFaker.number().numberBetween(5, 10)).collect(Collectors.toSet());

    public static InvoiceDataBuilder builder() {
        return new InvoiceDataBuilder();
    }

    public Invoice buildIssue(){
        return Invoice.issue(
                orderId.get(),
                customerId.get(),
                payer.get(),
                items.get()
        );
    }

}
