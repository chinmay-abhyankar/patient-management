package com.pm.patientservice.grpc;

import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc;
import com.pm.patientservice.exception.BillingServiceException;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

@Service
public class BillingServiceGrpcClient {
    private static final Logger log = LoggerFactory.getLogger(BillingServiceGrpcClient.class);
    // provides a synchronous call-to-billing service similar to REST API calls
    private final BillingServiceGrpc.BillingServiceBlockingStub blockingStub;

    private final ManagedChannel channel;

   public BillingServiceGrpcClient(
           @Value("${billing.service.address:localhost}") String serverAddress,
           @Value("${billing.service.grpc.port:9001}") int serverPort
   ){
        log.info("Connecting to billing service at {}:{}", serverAddress, serverPort);

       channel = ManagedChannelBuilder.forAddress(serverAddress, serverPort)
               .usePlaintext()
               .build();

       blockingStub = BillingServiceGrpc.newBlockingStub(channel);
   }

   public BillingResponse createBillingAccount(String patientId, String name, String email){
       BillingRequest billingRequest = BillingRequest.newBuilder()
               .setPatientId(patientId)
               .setEmail(email)
               .setName(name)
               .build();

       try {
           BillingResponse billingResponse = blockingStub.createBillingAccount(billingRequest);
           log.info("Billing account created for patient with id {}: {}", patientId, billingResponse);
           return billingResponse;
       } catch (StatusRuntimeException e) {
           String errorMessage;
           String errorCode = switch (e.getStatus().getCode()) {
               case ALREADY_EXISTS -> {
                   errorMessage = "Billing account already exists for patient " + patientId;
                   yield "BILLING_ACCOUNT_EXISTS";
               }
               case INVALID_ARGUMENT -> {
                   errorMessage = "Invalid billing information provided: " + e.getStatus().getDescription();
                   yield "INVALID_BILLING_INFO";
               }
               case DEADLINE_EXCEEDED -> {
                   errorMessage = "Billing service request timed out";
                   yield "BILLING_TIMEOUT";
               }
               case UNAVAILABLE -> {
                   errorMessage = "Billing service is currently unavailable";
                   yield "BILLING_SERVICE_DOWN";
               }
               case UNAUTHENTICATED -> {
                   errorMessage = "Authentication failed with billing service";
                   yield "BILLING_AUTH_FAILED";
               }
               default -> {
                   errorMessage = "Unexpected error occurred while creating billing account";
                   yield "BILLING_UNKNOWN_ERROR";
               }
           };

           log.error("Failed to create billing account for patient {}: {} ({})",
                   patientId, errorMessage, e.getStatus(), e);
           throw new BillingServiceException(errorMessage, errorCode, e);
       } catch (Exception e) {
           log.error("Unexpected error while creating billing account for patient {}", patientId, e);
           throw new BillingServiceException(
                   "Internal error while processing billing request",
                   "BILLING_INTERNAL_ERROR",
                   e
           );
       }

   }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down gRPC channel");
        try {
            if (!channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)) {
                log.warn("Channel did not terminate within timeout, forcing shutdown");
                channel.shutdownNow();
            }
        } catch (InterruptedException e) {
            log.warn("Error during gRPC channel shutdown", e);
            channel.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }



}
