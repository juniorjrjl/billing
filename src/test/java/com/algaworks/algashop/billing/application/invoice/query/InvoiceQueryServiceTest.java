package com.algaworks.algashop.billing.application.invoice.query;

import com.algaworks.algashop.billing.domain.model.invoice.InvoiceNotFoundException;
import com.algaworks.algashop.billing.domain.model.invoice.InvoiceRepository;
import com.algaworks.algashop.billing.utility.AbstractApplicationTest;
import com.algaworks.algashop.billing.utility.InvoiceDataBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
class InvoiceQueryServiceTest extends AbstractApplicationTest {

    private final InvoiceQueryService queryService;
    private final InvoiceRepository repository;

    @Autowired
    InvoiceQueryServiceTest(final JdbcTemplate jdbcTemplate,
                            final InvoiceQueryService queryService,
                            final InvoiceRepository repository) {
        super(jdbcTemplate);
        this.queryService = queryService;
        this.repository = repository;
    }

    @Test
    void shouldFindInvoiceByOrderId() {
        final var invoice = InvoiceDataBuilder.builder().buildIssue();
        repository.save(invoice);
        final var actual = queryService.findByOrderId(invoice.getOrderId());
        assertThat(actual.getOrderId()).isEqualTo(invoice.getOrderId());
    }

    @Test
    void giveNonStoredInvoiceIdWhenFindByOrderIdThenThrowException() {
        assertThatExceptionOfType(InvoiceNotFoundException.class)
                .isThrownBy(() -> queryService.findByOrderId(UUID.randomUUID().toString()));
    }

}