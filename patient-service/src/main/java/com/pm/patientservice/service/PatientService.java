package com.pm.patientservice.service;

import com.pm.patientservice.dto.PagedPatientResponseDto;
import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.exception.EmailAlreadyExistsException;
import com.pm.patientservice.exception.PatientNotFoundException;
import com.pm.patientservice.grpc.BillingServiceGrpcClient;
import com.pm.patientservice.kafka.KafkaProducer;
import com.pm.patientservice.mapper.PatientMapper;
import com.pm.patientservice.model.Patient;
import com.pm.patientservice.repository.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private static final Logger log = LoggerFactory.getLogger(PatientService.class);
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
     * This method fetches all patient records without pagination or filtering.
     *
     * @return List<PatientResponseDTO> - a list of patient response DTOs containing patient details
     */
    public List<PatientResponseDTO> getPatients(){
        List<Patient> patients = patientRepository.findAll();

        return patients.stream()
                .map(PatientMapper::toPatientResponseDTO)
                .toList();
    }

    /**
     * Retrieves a paginated list of patients based on the specified parameters.
     *
     * @param page        the page number to retrieve (zero-based)
     * @param size        the number of items per page
     * @param sort        the sort direction ("asc" or "desc")
     * @param sortField   the field to sort by
     * @param searchField the field to search in
     * @param searchValue the value to search for
     * @return a PagedPatientResponseDto containing the paginated list of patients
     */
    @Cacheable(
            value="patients",
            key = "#page + '-' + #size + '-' + #sort + '-' + #sortField",
            condition = "#searchValue == ''"
    )
    public PagedPatientResponseDto getPatients(Integer page, Integer size, String sort, String sortField, String searchField, String searchValue) {
        log.info("{REDIS} Cache miss - fetching from DB ");

        try{
            Thread.sleep(2000);
        }catch (InterruptedException e){
            log.error(e.getMessage());
        }

        Pageable pageable = PageRequest.of(page - 1, size,
                sort.equalsIgnoreCase("desc") ? Sort.by(sortField).descending()
                        : Sort.by(sortField).ascending());

        Page<Patient> patientPage;

        if (searchValue == null || searchValue.isBlank()) {
            patientPage = patientRepository.findAll(pageable);
        } else {
            patientPage = switch (searchField.toLowerCase()) {
                case "name" -> patientRepository.findByNameContainingIgnoreCase(searchValue, pageable);
                case "address" -> patientRepository.findByAddressContainingIgnoreCase(searchValue, pageable);
                case "email" -> patientRepository.findByEmailContainingIgnoreCase(searchValue, pageable);
                default -> Page.empty(pageable);
            };
        }

        List<PatientResponseDTO> patientResponseDTOS = patientPage.getContent()
                .stream().map(PatientMapper::toPatientResponseDTO).toList();


        return PagedPatientResponseDto.builder()
                .patients(patientResponseDTOS)
                .totalPages(patientPage.getTotalPages())
                .totalElements((int)patientPage.getTotalElements())
                .pageNumber(patientPage.getNumber()+1)
                .pageSize(patientPage.getSize())
                .build();
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
