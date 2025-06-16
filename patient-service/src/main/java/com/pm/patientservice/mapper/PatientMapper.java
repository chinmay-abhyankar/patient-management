package com.pm.patientservice.mapper;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.model.Patient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Maps Patient entity to PatientResponseDTO for data transfer.
 */
public class PatientMapper {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm:ss yyyy-MM-dd");

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static PatientResponseDTO toPatientResponseDTO(Patient patient){
        PatientResponseDTO patientResponseDTO = new PatientResponseDTO();
        patientResponseDTO.setId(patient.getId().toString());
        patientResponseDTO.setName(patient.getName());
        patientResponseDTO.setEmail(patient.getEmail());
        patientResponseDTO.setAddress(patient.getAddress());
        patientResponseDTO.setDateOfBirth(patient.getDateOfBirth().toString());
        return patientResponseDTO;
    }

    /**
     * Converts a PatientRequestDTO object into a Patient entity.
     * This method maps the data from the DTO to the Patient entity,
     * performing the necessary validations on date fields.
     *
     * @param patientRequestDTO the DTO object containing patient data to be mapped
     * @return a Patient entity with data mapped from the provided PatientRequestDTO
     * @throws IllegalArgumentException if any date fields are in an invalid format or out of valid range
     */
    public static Patient toPatientEntity(PatientRequestDTO patientRequestDTO){
        Patient patient = new Patient();
        patient.setAddress(patientRequestDTO.getAddress());
        try{
            LocalDateTime registerDate = LocalDateTime.parse(
                    patientRequestDTO.getRegistrationDate(),
                    DATE_TIME_FORMATTER
            );
            validateDateTime(registerDate);
            patient.setRegisteredDate(registerDate);
        }catch (DateTimeParseException e){
            throw new IllegalArgumentException("Invalid format for register date . Please use "+DATE_TIME_FORMATTER+" format ");
        }

        patient.setEmail(patientRequestDTO.getEmail());
        patient.setName(patientRequestDTO.getName());
        try {
            LocalDate dob = LocalDate.parse(patientRequestDTO.getDateOfBirth());
            validateDate(dob);
            patient.setDateOfBirth(dob);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid format for date of birth . Please use yyyy-MM-dd format");
        }
        return patient;
    }

    public static Patient updatePatientEntity(Patient patient, PatientRequestDTO patientRequestDTO){
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setName(patientRequestDTO.getName());
        patient.setEmail(patientRequestDTO.getEmail());
        try {
            LocalDate dob = LocalDate.parse(patientRequestDTO.getDateOfBirth());
            validateDate(dob);
            patient.setDateOfBirth(dob);
        }
        catch (DateTimeParseException e){
            throw new IllegalArgumentException("Invalid format for date of birth . Please use yyyy-MM-dd format");
        }
        return patient;
    }

    private static void validateDate(LocalDate date) {
        LocalDate minDate = LocalDate.of(1900, 1, 1);
        LocalDate maxDate = LocalDate.now();

        if (date.isBefore(minDate) || date.isAfter(maxDate)) {
            throw new IllegalArgumentException(
                    "Date of birth must be between 1950-01-01 and " + DATE_FORMATTER.format(maxDate)
            );
        }
    }

    private static void validateDateTime(LocalDateTime date) {
        LocalDateTime minDate = LocalDateTime.of(1900, 1, 1,0,0,0);
        LocalDateTime maxDate = LocalDateTime.now();

        if (date.isBefore(minDate) || date.isAfter(maxDate)) {
            throw new IllegalArgumentException(
                    "Date of birth must be between 1950-01-01 and " + DATE_TIME_FORMATTER.format(maxDate)
            );
        }
    }
}
