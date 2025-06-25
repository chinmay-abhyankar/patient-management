package com.pm.patientservice.dto;

import com.pm.patientservice.dto.validators.CreatePatientValidationGroup;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) for patient creation and update requests.
 * <p>
 * This class is used to encapsulate the data required to create or update a patient record.
 * It includes validation annotations to ensure the integrity and correctness of the input data.
 * </p>
 *
 * <ul>
 *   <li><b>name</b>: Patient's full name (required, 3-50 characters).</li>
 *   <li><b>email</b>: Patient's email address (required, must be valid format).</li>
 *   <li><b>address</b>: Patient's address (required, 5-100 characters).</li>
 *   <li><b>dateOfBirth</b>: Patient's date of birth (required).</li>
 *   <li><b>registrationDate</b>: Date when the patient was registered (required for creation).</li>
 * </ul>
 *
 * <p>
 * Example usage:
 * <pre>{@code
 * PatientRequestDTO dto = new PatientRequestDTO();
 * dto.setName("John Doe");
 * dto.setEmail("john.doe@example.com");
 * dto.setAddress("123 Main St");
 * dto.setDateOfBirth("1990-01-01");
 * dto.setRegistrationDate("2024-06-23");
 * }</pre>
 * </p>
 */
public class PatientRequestDTO {
    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
    private String name;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Address is required")
    @Size(min = 5, max = 100, message = "Address must be between 5 and 255 characters")
    private String address;

    @NotNull(message = "Date of birth is required")
    private String dateOfBirth;

    @NotBlank(groups = CreatePatientValidationGroup.class, message = "Registration date is required")
    private String registrationDate;

    /**
     * Gets the patient's name.
     * @return the name of the patient
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the patient's name.
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the patient's email address.
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the patient's email address.
     * @param email the email address to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the patient's address.
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the patient's address.
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Gets the patient's date of birth.
     * @return the date of birth
     */
    public String getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * Sets the patient's date of birth.
     * @param dateOfBirth the date of birth to set
     */
    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    /**
     * Gets the patient's registration date.
     * @return the registration date
     */
    public String getRegistrationDate() {
        return registrationDate;
    }

    /**
     * Sets the patient's registration date.
     * @param registrationDate the registration date to set
     */
    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }
}