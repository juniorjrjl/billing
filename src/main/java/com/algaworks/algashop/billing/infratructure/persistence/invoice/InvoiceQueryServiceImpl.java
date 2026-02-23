package com.algaworks.algashop.billing.infratructure.persistence.invoice;

import com.algaworks.algashop.billing.application.invoice.query.InvoiceOutput;
import com.algaworks.algashop.billing.application.invoice.query.InvoiceQueryService;
import com.algaworks.algashop.billing.domain.model.invoice.InvoiceNotFoundException;
import com.algaworks.algashop.billing.domain.model.invoice.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InvoiceQueryServiceImpl implements InvoiceQueryService {

    private final InvoiceRepository repository;
    private final InvoiceDisassembler disassembler;

    @Override
    public InvoiceOutput findByOrderId(final String orderId) {
        return repository.findByOrderId(orderId).map(disassembler::toOutput)
                .orElseThrow(InvoiceNotFoundException::new);
    }
}
