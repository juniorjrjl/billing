package com.algaworks.algashop.billing.domain.model;

import lombok.NoArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;

import java.math.BigDecimal;

import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class FieldValidations {

    public static void validateEmail(final String email, final String message) {
        if (emailIsInValid(email)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static boolean emailIsInValid(final String email) {
        return (email.isBlank() || !EmailValidator.getInstance().isValid(email));
    }

    public static String requireNonBlank(final String value) {
        if (requireNonNull(value).isBlank()) {
            throw new IllegalArgumentException();
        }
        return value;
    }

    public static String requireNonBlank(final String value, final String message) {
        if (requireNonNull(value, message).isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    public static Integer requireGreaterThanZero(final Integer value) {
        if (requireNonNull(value) <= 0){
            throw new IllegalArgumentException();
        }
        return value;
    }

    public static BigDecimal requireGreaterThanZero(final BigDecimal value) {
        if (requireNonNull(value).compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException();
        }
        return value;
    }

}