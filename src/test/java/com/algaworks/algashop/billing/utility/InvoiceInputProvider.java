package com.algaworks.algashop.billing.utility;

import com.algaworks.algashop.billing.application.invoice.managment.AddressData;
import com.algaworks.algashop.billing.application.invoice.managment.LineItemInput;
import com.algaworks.algashop.billing.application.invoice.managment.PayerData;
import com.algaworks.algashop.billing.application.invoice.managment.PaymentSettingsInput;
import com.algaworks.algashop.billing.domain.model.invoice.PaymentMethod;
import com.algaworks.algashop.billing.domain.model.invoice.payment.Payment;
import com.algaworks.algashop.billing.domain.model.invoice.payment.PaymentStatus;
import net.datafaker.providers.base.AbstractProvider;

import java.util.UUID;

public class InvoiceInputProvider extends AbstractProvider<CustomFaker> {

    protected InvoiceInputProvider(final CustomFaker faker) {
        super(faker);
    }

    public LineItemInput lineItem() {
        return new LineItemInput(
                faker.boardgame().name(),
                faker.numeric().valueBetween(5, 30)
        );
    }

    public PayerData payer(){
        return PayerData.builder()
                .fullName(faker.name().fullName())
                .document(faker.cpf().valid())
                .email(faker.internet().emailAddress())
                .phone(faker.phoneNumber().cellPhone())
                .address(faker.bool().bool() ?  address() : addressWithComplement())
                .build();
    }

    public AddressData address(){
        return AddressData.builder()
                .street(faker.address().streetAddress())
                .neighborhood(faker.lorem().characters())
                .number(faker.address().streetAddressNumber())
                .city(faker.address().city())
                .state(faker.address().state())
                .zipCode(faker.address().zipCode())
                .build();
    }

    public AddressData addressWithComplement(){
        return AddressData.builder()
                .street(faker.address().streetAddress())
                .complement(faker.address().buildingNumber())
                .neighborhood(faker.lorem().characters())
                .number(faker.address().streetAddressNumber())
                .city(faker.address().city())
                .state(faker.address().state())
                .zipCode(faker.address().zipCode())
                .build();
    }

    public PaymentSettingsInput paymentSettings() {
        return PaymentSettingsInput.builder()
                .method(faker.option(PaymentMethod.class))
                .creditCardId(UUID.randomUUID())
                .build();
    }

    public Payment payment() {
        return Payment.builder()
                .gatewayCode(faker.number().digits(10))
                .invoiceId(UUID.randomUUID())
                .method(faker.option(PaymentMethod.class))
                .status(faker.option(PaymentStatus.class))
                .build();
    }

}
