
package com.pm.patientservice.exception;

public class BillingServiceException extends RuntimeException {
    private final String errorCode;

    public BillingServiceException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public BillingServiceException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}