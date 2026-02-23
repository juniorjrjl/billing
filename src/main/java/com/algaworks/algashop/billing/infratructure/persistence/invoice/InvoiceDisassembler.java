package com.algaworks.algashop.billing.infratructure.persistence.invoice;

import com.algaworks.algashop.billing.application.invoice.query.InvoiceOutput;
import com.algaworks.algashop.billing.domain.model.invoice.Invoice;
import org.jspecify.annotations.NullMarked;
import org.mapstruct.AnnotateWith;
import org.mapstruct.Mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@AnnotateWith(NullMarked.class)
@Mapper(componentModel = SPRING)
public interface InvoiceDisassembler {

    InvoiceOutput toOutput(final Invoice invoice);

}
