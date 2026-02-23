package com.algaworks.algashop.billing.domain.model.invoice;

import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Objects;

import static com.algaworks.algashop.billing.domain.model.FieldValidations.requireGreaterThanZero;
import static com.algaworks.algashop.billing.domain.model.FieldValidations.requireNonBlank;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Embeddable
@Getter
@Setter(PRIVATE)
@NoArgsConstructor(access = PROTECTED)
public class LineItem {

    private Integer number;
    private String name;
    private BigDecimal amount;

    @Builder
    public LineItem(final Integer number,
                    final String name,
                    final BigDecimal amount) {
        this.number = requireGreaterThanZero(number);
        this.name = requireNonBlank(name);
        this.amount = requireGreaterThanZero(amount);
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof LineItem lineItem)) return false;
        return Objects.equals(number, lineItem.number) &&
                Objects.equals(name, lineItem.name) &&
                Objects.equals(amount, lineItem.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, name, amount);
    }
}
