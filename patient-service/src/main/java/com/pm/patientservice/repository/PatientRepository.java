package com.pm.patientservice.repository;

import com.pm.patientservice.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository interface for performing database operations on the Patient entity.
 * It extends {@link JpaRepository} to provide CRUD operations and additional query
 * capabilities for the Patient entity, with UUID as the ID type.
 *
 * This interface serves as a part of the persistence layer and enables interaction
 * with the underlying database for Patient-related data.
 *
 * As a Spring Data JPA repository, it automatically provides implementations for
 * common database operations, such as saving, deleting, and finding entities.
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {
    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, UUID id);
}
