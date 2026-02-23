package com.algaworks.algashop.billing.application.invoice.managment;

import com.algaworks.algashop.billing.domain.model.invoice.Invoice;
import com.algaworks.algashop.billing.domain.model.invoice.LineItem;
import com.algaworks.algashop.billing.domain.model.invoice.Payer;
import com.algaworks.algashop.billing.domain.model.invoice.payment.PaymentRequest;
import org.jspecify.annotations.NullMarked;
import org.mapstruct.AnnotateWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@AnnotateWith(NullMarked.class)
@Mapper(componentModel = SPRING)
public interface InvoiceInputDisassembler {

    Payer toDomain(final PayerData data);

    default Set<LineItem> toDomain(final Set<LineItemInput> inputs){
        if (inputs.isEmpty()) {
            return Collections.emptySet();
        }
        final List<LineItemInput> inputList = inputs.stream().toList();
        final Set<LineItem> domains = LinkedHashSet.newLinkedHashSet( inputs.size() );
        IntStream.range(0, inputs.size())
                .forEach(i -> domains.add(toDomain(inputList.get(i), i + 1)));
        return domains;
    }

    LineItem toDomain(final LineItemInput input, final int number);


    @Mapping(target = "amount", source = "totalAmount")
    @Mapping(target = "paymentMethod", source = "paymentSettings.paymentMethod")
    @Mapping(target = "creditCardId", source = "paymentSettings.creditCardId")
    @Mapping(target = "invoiceId", source = "id")
    PaymentRequest toCaptureRequest(final Invoice domain);

}
