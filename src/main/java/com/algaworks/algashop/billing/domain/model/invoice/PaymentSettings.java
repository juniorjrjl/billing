package com.algaworks.algashop.billing.domain.model.invoice;


import com.algaworks.algashop.billing.domain.model.IdGenerator;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

import static com.algaworks.algashop.billing.domain.model.invoice.PaymentMethod.CREDIT_CARD;
import static jakarta.persistence.EnumType.STRING;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Table(name = "PAYMENT_SETTINGS")
@Entity
@Setter(PRIVATE)
@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PROTECTED)
public class PaymentSettings {

    @Id
    private UUID id;
    @Nullable
    private UUID creditCardId;
    @Nullable
    private String gatewayCode;
    @Enumerated(STRING)
    private PaymentMethod paymentMethod;

    @OneToOne(mappedBy = "paymentSettings")
    @Getter(PRIVATE)
    @Setter(PROTECTED)
    private Invoice invoice;

    static PaymentSettings brandNew(final PaymentMethod method,
                                    @Nullable
                                    final UUID creditCard,
                                    final Invoice invoice) {
        if (method == CREDIT_CARD){
            requireNonNull(creditCard);
        }
        return new PaymentSettings(
                IdGenerator.generateTimeBasedUUID(),
                creditCard,
                null,
                method,
                invoice
        );
    }

    void assignGatewayCode(final String gatewayCode) {
        if (nonNull(this.getGatewayCode())){
            throw new IllegalArgumentException("Gateway code already assigned");
        }
        if (StringUtils.isBlank(gatewayCode)) {
            throw new IllegalArgumentException("Gateway code cannot be blank");
        }
        setGatewayCode(gatewayCode);
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof PaymentSettings that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
