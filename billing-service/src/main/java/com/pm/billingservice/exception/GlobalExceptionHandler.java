package com.pm.billingservice.exception;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ... existing handlers ...

    @ExceptionHandler(BillingValidationException.class)
    public ResponseEntity<Map<String, String>> handleBillingValidationException(BillingValidationException ex) {
        log.warn("Billing validation error: {} (Code: {})", ex.getMessage(), ex.getErrorCode());
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        errors.put("errorCode", ex.getErrorCode());
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(BillingAccountExistsException.class)
    public ResponseEntity<Map<String, String>> handleBillingAccountExistsException(BillingAccountExistsException ex) {
        log.warn("Billing account already exists: {} (Code: {})", ex.getMessage(), ex.getErrorCode());
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        errors.put("errorCode", ex.getErrorCode());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errors);
    }

    // Keep the general BillingServiceException handler for other billing-related errors
    @ExceptionHandler(BillingServiceException.class)
    public ResponseEntity<Map<String, String>> handleBillingServiceException(BillingServiceException ex) {
        log.error("Billing service error: {} (Code: {})", ex.getMessage(), ex.getErrorCode(), ex);
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        errors.put("errorCode", ex.getErrorCode());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errors);
    }
}