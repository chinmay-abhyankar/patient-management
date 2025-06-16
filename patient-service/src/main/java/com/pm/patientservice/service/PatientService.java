package com.pm.patientservice.service;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.exception.EmailAlreadyExistsException;
import com.pm.patientservice.exception.PatientNotFoundException;
import com.pm.patientservice.grpc.BillingServiceGrpcClient;
import com.pm.patientservice.kafka.KafkaProducer;
import com.pm.patientservice.mapper.PatientMapper;
import com.pm.patientservice.model.Patient;
import com.pm.patientservice.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service class for managing patient-related operations.
 * This class acts as an intermediary between the controller and the repository layers,
 * implementing the business logic for handling Patient entities.
 *
 * Annotations:
 * - @Service: Indicates that the class is a service component in the Spring framework.
 *
 * Dependencies:
 * - {@link PatientRepository}: Used to interact with the database for patient-related data.
 * - {@link PatientMapper}: Used for converting between entities and DTOs.
 *
 * Responsibilities:
 * - Provides methods to retrieve and create patient records.
 */
@Service
public class PatientService {
    private final PatientRepository patientRepository;
    private final BillingServiceGrpcClient billingServiceGrpcClient;
    private  final KafkaProducer kafkaProducer;

    PatientService(PatientRepository patientRepository, BillingServiceGrpcClient billingServiceGrpcClient,KafkaProducer kafkaProducer) {
        this.patientRepository = patientRepository;
        this.billingServiceGrpcClient = billingServiceGrpcClient;
        this.kafkaProducer = kafkaProducer;
    }

    /**
     * Retrieves a list of all patients from the repository and maps them to PatientResponseDTO objects.
     *
     * @return a list of PatientResponseDTO objects representing the patients in the system.
     */
    public List<PatientResponseDTO> getPatients(){
        List<Patient> patients = patientRepository.findAll();

        return patients.stream()
                .map(PatientMapper::toPatientResponseDTO)
                .toList();
    }

    /**
     * Creates a new patient record in the system based on the provided details.
     * Maps the input data to a Patient entity, persists it, and returns the
     * corresponding response DTO.
     *
     * @param patientRequestDTO the data transfer object containing the patient's
     *                          details to be saved
     * @return a PatientResponseDTO representing the newly created patient
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class,
            isolation = Isolation.READ_COMMITTED,noRollbackFor = PatientNotFoundException.class)
    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO){
        boolean isPresent = patientRepository.existsByEmail(patientRequestDTO.getEmail());

        if(isPresent){
            throw new EmailAlreadyExistsException("Patient with this email exist : " + patientRequestDTO.getEmail() + " choose different email");
        }

        Patient patient = patientRepository.save(PatientMapper.toPatientEntity(patientRequestDTO));

        if(patient.getId() != null){
            billingServiceGrpcClient.createBillingAccount(patient.getId().toString(),patient.getName(),patient.getEmail());

            kafkaProducer.sendPatientCreatedEvent(patient);
        }

        return PatientMapper.toPatientResponseDTO(patient);
    }

    public PatientResponseDTO updatePatient(UUID id, PatientRequestDTO patientRequestDTO){
        Patient patient = patientRepository.findById(id).orElseThrow(
                () -> new PatientNotFoundException("Patient with id " + id + " not found")
        );

        boolean isPresent = patientRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(),id);

        if(isPresent){
            throw new EmailAlreadyExistsException("Patient with this email exist : " + patientRequestDTO.getEmail() + " choose different email");
        }

        return  PatientMapper.toPatientResponseDTO(
                PatientMapper.updatePatientEntity(patient, patientRequestDTO));
    }

    public void deletePatient(UUID id){
        Patient patient = patientRepository.findById(id).orElseThrow(
                () -> new PatientNotFoundException("Patient with id " + id + " not found")
        );

        patientRepository.delete(patient);
    }


}
