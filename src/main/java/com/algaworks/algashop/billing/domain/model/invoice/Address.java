package com.algaworks.algashop.billing.domain.model.invoice;

import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

import static com.algaworks.algashop.billing.domain.model.FieldValidations.requireNonBlank;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Embeddable
@Getter
@Setter(PRIVATE)
@NoArgsConstructor(access = PROTECTED)
public class Address {

    private String street;
    private String number;
    @Nullable
    private String complement;
    private String neighborhood;
    private String city;
    private String state;
    private String zipCode;

    @Builder
    public Address(final String street,
                   final String number,
                   @Nullable
                   final String complement,
                   final String neighborhood,
                   final String city,
                   final String state,
                   final String zipCode) {
        this.street = requireNonBlank(street);
        this.number = requireNonBlank(number);
        this.complement = complement;
        this.neighborhood = requireNonBlank(neighborhood);
        this.city = requireNonBlank(city);
        this.state = requireNonBlank(state);
        this.zipCode = requireNonBlank(zipCode);
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Address address)) return false;
        return Objects.equals(street, address.street) &&
                Objects.equals(number, address.number) &&
                Objects.equals(complement, address.complement) &&
                Objects.equals(neighborhood, address.neighborhood) &&
                Objects.equals(city, address.city) &&
                Objects.equals(state, address.state) &&
                Objects.equals(zipCode, address.zipCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, number, complement, neighborhood, city, state, zipCode);
    }

}
