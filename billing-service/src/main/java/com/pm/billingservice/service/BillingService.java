package com.pm.billingservice.service;

import com.pm.billingservice.exception.BillingAccountExistsException;
import com.pm.billingservice.model.BillingAccount;
import com.pm.billingservice.repository.BillingAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BillingService {
    private final BillingAccountRepository billingAccountRepository;

    public BillingService(BillingAccountRepository billingAccountRepository) {
        this.billingAccountRepository = billingAccountRepository;
    }

    @Transactional
    public BillingAccount createBillingAccount(String patientId, String patientName, String patientEmail) {
        if (billingAccountRepository.existsByPatientId(patientId)) {
            throw new BillingAccountExistsException("Billing account already exists for patient: " + patientId);
        }

        BillingAccount billingAccount = new BillingAccount();
        billingAccount.setPatientId(patientId);
        billingAccount.setPatientName(patientName);
        billingAccount.setPatientEmail(patientEmail);

        return billingAccountRepository.save(billingAccount);
    }
}