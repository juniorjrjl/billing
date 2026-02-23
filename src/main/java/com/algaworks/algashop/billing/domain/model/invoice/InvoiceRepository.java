package com.algaworks.algashop.billing.domain.model.invoice;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {

    boolean existsByOrderId(final String orderId);

    Optional<Invoice> findByOrderId(final String orderId);
}
