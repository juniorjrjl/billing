package com.algaworks.algashop.billing.infratructure.listener;

import com.algaworks.algashop.billing.domain.model.invoice.InvoiceCanceledEvent;
import com.algaworks.algashop.billing.domain.model.invoice.InvoiceIssuedEvent;
import com.algaworks.algashop.billing.domain.model.invoice.InvoicePaidEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class InvoiceEventListener {

    @EventListener
    public void listen(final InvoiceIssuedEvent event) {}

    @EventListener
    public void listen(final InvoicePaidEvent event) {}

    @EventListener
    public void listen(final InvoiceCanceledEvent event) {}

}
