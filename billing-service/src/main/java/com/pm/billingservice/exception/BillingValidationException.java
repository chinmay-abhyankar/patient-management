package com.pm.billingservice.exception;

public class BillingValidationException extends BillingServiceException {
    public BillingValidationException(String message) {
        super(message, "BILLING-001");
    }
}