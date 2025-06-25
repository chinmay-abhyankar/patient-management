package com.pm.patientservice.dto;

/**
 * Data Transfer Object (DTO) for sending patient information in responses.
 * <p>
 * This class encapsulates the patient data returned to the client, such as
 * patient ID, name, email, address, and date of birth.
 * </p>
 *
 * <ul>
 *   <li><b>id</b>: Unique identifier of the patient.</li>
 *   <li><b>name</b>: Patient's full name.</li>
 *   <li><b>email</b>: Patient's email address.</li>
 *   <li><b>address</b>: Patient's address.</li>
 *   <li><b>dateOfBirth</b>: Patient's date of birth.</li>
 * </ul>
 *
 * <p>
 * Example usage:
 * <pre>{@code
 * PatientResponseDTO dto = new PatientResponseDTO();
 * dto.setId("123");
 * dto.setName("Jane Doe");
 * dto.setEmail("jane.doe@example.com");
 * dto.setAddress("456 Main St");
 * dto.setDateOfBirth("1992-05-10");
 * }</pre>
 * </p>
 */
public class PatientResponseDTO {
    private String id;
    private String name;
    private String email;
    private String address;
    private String dateOfBirth;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getDateOfBirth() {
        return dateOfBirth;
    }
    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}