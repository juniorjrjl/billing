package com.algaworks.algashop.billing.utility;

import com.algaworks.algashop.billing.domain.model.invoice.Address;
import com.algaworks.algashop.billing.domain.model.invoice.LineItem;
import com.algaworks.algashop.billing.domain.model.invoice.Payer;
import com.algaworks.algashop.billing.domain.model.invoice.PaymentMethod;
import com.algaworks.algashop.billing.domain.model.invoice.PaymentSettings;
import net.datafaker.providers.base.AbstractProvider;

import java.util.UUID;

public class InvoiceProvider extends AbstractProvider<CustomFaker> {

    protected InvoiceProvider(final CustomFaker faker) {
        super(faker);
    }

    public LineItem lineItem() {
        return new LineItem(
                faker.number().positive(),
                faker.boardgame().name(),
                faker.numeric().valueBetween(5, 30)
        );
    }

    public Payer payer(){
        return Payer.builder()
                .fullName(faker.name().fullName())
                .document(faker.cpf().valid())
                .email(faker.internet().emailAddress())
                .phone(faker.phoneNumber().cellPhone())
                .address(faker.bool().bool() ?  address() : addressWithComplement())
                .build();
    }

    public Address address(){
        return Address.builder()
                .street(faker.address().streetAddress())
                .neighborhood(faker.lorem().characters())
                .number(faker.address().streetAddressNumber())
                .city(faker.address().city())
                .state(faker.address().state())
                .zipCode(faker.address().zipCode())
                .build();
    }

    public Address addressWithComplement(){
        return Address.builder()
                .street(faker.address().streetAddress())
                .complement(faker.address().buildingNumber())
                .neighborhood(faker.lorem().characters())
                .number(faker.address().streetAddressNumber())
                .city(faker.address().city())
                .state(faker.address().state())
                .zipCode(faker.address().zipCode())
                .build();
    }
}
