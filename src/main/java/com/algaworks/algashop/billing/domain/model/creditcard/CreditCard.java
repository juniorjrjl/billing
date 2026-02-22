package com.algaworks.algashop.billing.domain.model.creditcard;

import com.algaworks.algashop.billing.domain.model.IdGenerator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Setter(PRIVATE)
@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PROTECTED)
public class CreditCard {

    private UUID id;
    private OffsetDateTime createdAt;
    private UUID customerId;
    private String lastNumbers;
    private String brand;
    private Integer expirationMonth;
    private Integer expirationYear;
    private String gatewayCode;

    public static CreditCard brandNew(final UUID customerId,
                                      final String lastNumbers,
                                      final String brand,
                                      final Integer expirationMonth,
                                      final Integer expirationYear,
                                      final String gatewayCreditCardCode) {
        if (expirationMonth < 1 || expirationMonth > 12) {
            throw new IllegalArgumentException();
        }
        if (StringUtils.isAnyBlank(lastNumbers, brand, gatewayCreditCardCode)) {
            throw new IllegalArgumentException();
        }
        return new CreditCard(
                IdGenerator.generateTimeBasedUUID(),
                OffsetDateTime.now(),
                customerId,
                lastNumbers,
                brand,
                expirationMonth,
                expirationYear,
                gatewayCreditCardCode
        );
    }

    public void setGatewayCode(final String gatewayCode) {
        if  (StringUtils.isBlank(gatewayCode)) {
            throw new IllegalArgumentException();
        }
        this.gatewayCode = gatewayCode;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof CreditCard that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
