package com.pm.billingservice.kafka;

import billing.events.BillingAccountEvent;
import com.google.protobuf.InvalidProtocolBufferException;
import com.pm.billingservice.service.BillingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {
    private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);

    private final BillingService billingService;

    public KafkaConsumer(BillingService billingService){
            this.billingService = billingService;
    }

    @KafkaListener(topics = "billing-account", groupId = "billing-service")
    public void consumeEvent(byte[] event) {
        try {
            BillingAccountEvent billingAccountEvent = BillingAccountEvent.parseFrom(event);
            log.info("Received billing account event : [PatientId={},PatientName={},PatientEmail={}] "
                    , billingAccountEvent.getPatientId(), billingAccountEvent.getName(), billingAccountEvent.getEmail());

            billingService.createBillingAccount(billingAccountEvent.getPatientId(), billingAccountEvent.getName(), billingAccountEvent.getEmail());
        } catch (InvalidProtocolBufferException e) {
            log.error("Error parsing billing account event : {} ", e.getMessage());
        }
    }
}
