package com.pm.patientservice.model;

import com.pm.patientservice.dto.validators.CreatePatientValidationGroup;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a patient entity in the system. This class is annotated as an entity
 * for persistence with a relational database using JPA. It includes information
 * about the patient such as name, email, phone number, address, date of birth,
 * and registration date.
 *
 * Fields in this class:
 * - id: Unique identifier for the patient (UUID).
 * - name: Full name of the patient (cannot be null).
 * - email: Email address of the patient, must be unique and valid (cannot be null).
 * - phone: Contact phone number of the patient (cannot be null).
 * - address: Residential address of the patient (cannot be null).
 * - dateOfBirth: Date of birth of the patient (cannot be null).
 * - registeredDate: Date and time when the patient was registered in the system (cannot be null).
 *
 * This class includes no-argument constructors, getter, and setter methods for
 * each field, allowing for encapsulation and controlled access to the entity data.
 */
@Entity
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotNull
    private String name;

    @NotNull
    @Email
    @Column(unique = true)
    private String email;

    @NotNull
    private String address;

    @NotNull
    private LocalDate dateOfBirth;

    @NotNull(groups = CreatePatientValidationGroup.class,message = "Registration date is required")
    private LocalDateTime registeredDate;

    public Patient() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
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

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public LocalDateTime getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(LocalDateTime registeredDate) {
        this.registeredDate = registeredDate;
    }
}
