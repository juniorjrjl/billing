package com.algaworks.algashop.billing.domain.model.invoice;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

import static com.algaworks.algashop.billing.domain.model.FieldValidations.requireNonBlank;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Embeddable
@Getter
@Setter(PRIVATE)
@NoArgsConstructor(access = PROTECTED)
public class Payer {

    private String fullName;
    private String document;
    private String phone;
    private String email;
    @Embedded
    private Address address;

    @Builder
    public Payer(final String fullName,
                 final String document,
                 final String phone,
                 final String email,
                 final Address address) {
        this.fullName = requireNonBlank(fullName);
        this.document = requireNonBlank(document);
        this.phone = requireNonBlank(phone);
        this.email = requireNonBlank(email);
        this.address = address;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Payer payer)) return false;
        return Objects.equals(fullName, payer.fullName) &&
                Objects.equals(document, payer.document) &&
                Objects.equals(phone, payer.phone) &&
                Objects.equals(email, payer.email) &&
                Objects.equals(address, payer.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullName, document, phone, email, address);
    }
}
