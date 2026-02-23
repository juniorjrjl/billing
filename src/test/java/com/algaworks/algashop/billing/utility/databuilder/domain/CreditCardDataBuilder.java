package com.algaworks.algashop.billing.utility.databuilder.domain;

import com.algaworks.algashop.billing.domain.model.creditcard.CreditCard;
import com.algaworks.algashop.billing.utility.CustomFaker;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.util.UUID;
import java.util.function.Supplier;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class CreditCardDataBuilder {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    @With
    private Supplier<UUID> customerId = UUID::randomUUID;
    @With
    private Supplier<String> lastNumbers = () -> customFaker.number().digits(4);
    @With
    private Supplier<String> brand = () -> customFaker.subscription().paymentMethods();
    @With
    private Supplier<Integer> expirationMonth = () -> customFaker.number().numberBetween(1, 13);
    @With
    private Supplier<Integer> expirationYear = () -> customFaker.number().numberBetween(30, 50);
    @With
    private Supplier<String> gatewayCode = () -> customFaker.number().digits(4);

    public static CreditCardDataBuilder builder(){
        return new CreditCardDataBuilder();
    }

    public CreditCard build(){
        return CreditCard.brandNew(
                customerId.get(),
                lastNumbers.get(),
                brand.get(),
                expirationMonth.get(),
                expirationYear.get(),
                gatewayCode.get()
        );
    }

}
