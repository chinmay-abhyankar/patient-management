package com.pm.billingservice.exception;

public class BillingAccountExistsException extends BillingServiceException {
    public BillingAccountExistsException(String message) {
        super(message, "BILLING-002");
    }
}