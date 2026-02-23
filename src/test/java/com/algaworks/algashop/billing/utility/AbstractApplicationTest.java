package com.algaworks.algashop.billing.utility;

import com.algaworks.algashop.billing.utility.tag.IntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;

@IntegrationTest
public abstract class AbstractApplicationTest {

    protected final CustomFaker customFaker = CustomFaker.getInstance();
    protected final JdbcTemplate jdbcTemplate;
    private final String[] TABLES = {"CREDIT_CARDS", "INVOIVE_LINES_ITEMS", "INVOICES", "PAYMENT_SETTINGS"};

    public AbstractApplicationTest(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @AfterEach
    void tearDown() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, TABLES);
    }

}