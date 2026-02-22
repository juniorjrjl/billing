package com.algaworks.algashop.billing.domain.model;

public class DomainException extends RuntimeException {

    public DomainException() {
        super();
    }

    public DomainException(String message) {
        super(message);
    }


    public DomainException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public DomainException(final Throwable cause) {
        super(cause);
    }

    protected DomainException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
