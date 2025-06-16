package com.pm.billingservice.grpc;

import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc.BillingServiceImplBase;
import com.pm.billingservice.exception.BillingAccountExistsException;
import com.pm.billingservice.exception.BillingServiceException;
import com.pm.billingservice.exception.BillingValidationException;
import com.pm.billingservice.model.BillingAccount;
import com.pm.billingservice.service.BillingService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcService
public class BillingGrpcService extends BillingServiceImplBase {
    private static final Logger log = LoggerFactory.getLogger(BillingGrpcService.class);
    private final BillingService billingService;

    public BillingGrpcService(BillingService billingService) {
        this.billingService = billingService;
    }

    @Override
    public void createBillingAccount(BillingRequest request,
                                     StreamObserver<BillingResponse> responseObserver) {
        try {
            validateRequest(request);

            log.info("Creating billing account request for patient: {}", request.getPatientId());

            //save to billing account table
            BillingAccount account = billingService.createBillingAccount(
                    request.getPatientId(),
                    request.getName(),
                    request.getEmail()
            );

            //billing grpc response
            BillingResponse response = BillingResponse.newBuilder()
                    .setAccountId(account.getId().toString())
                    .setStatus(account.getStatus().toString())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (BillingValidationException e) {
            log.warn("Validation error: {}", e.getMessage());
            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription(e.getMessage())
                            .asException()
            );
        } catch (BillingAccountExistsException e) {
            log.warn("Account already exists: {}", e.getMessage());
            responseObserver.onError(
                    Status.ALREADY_EXISTS
                            .withDescription(e.getMessage())
                            .asException()
            );
        } catch (BillingServiceException e) {
            log.error("Billing service error: {}", e.getMessage(), e);
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("Billing service error")
                            .asException()
            );
        } catch (Exception e) {
            log.error("Unexpected error creating billing account", e);
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("Internal server error")
                            .asException()
            );
        }

    }


    private void validateRequest(BillingRequest request) {
        if (request == null) {
            throw new BillingValidationException("Request cannot be null");
        }
        if (request.getPatientId() == null || request.getPatientId().trim().isEmpty()) {
            throw new BillingValidationException("Patient ID is required");
        }
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new BillingValidationException("Name is required");
        }
        if (request.getEmail() == null || !isValidEmail(request.getEmail())) {
            throw new BillingValidationException("Valid email is required");
        }
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

}

