package com.pm.patientservice.dto;

import com.pm.patientservice.dto.PatientResponseDTO;

import java.util.List;

public class PagedPatientResponseDto {
    private final List<PatientResponseDTO> patients;
    private final int totalPages;
    private final int totalElements;
    private final int pageNumber;
    private final int pageSize;

    public List<PatientResponseDTO> getPatients() {
        return patients;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    private PagedPatientResponseDto(List<PatientResponseDTO> patients, int totalPages, int totalElements, int pageNumber, int pageSize) {
        this.patients = patients;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<PatientResponseDTO> patients;
        private int totalPages;
        private int totalElements;
        private int pageNumber;
        private int pageSize;

        public Builder patients(List<PatientResponseDTO> patients) {
            this.patients = patients;
            return this;
        }

        public Builder totalPages(int totalPages) {
            this.totalPages = totalPages;
            return this;
        }

        public Builder totalElements(int totalElements) {
            this.totalElements = totalElements;
            return this;
        }

        public Builder pageNumber(int pageNumber) {
            this.pageNumber = pageNumber;
            return this;
        }

        public Builder pageSize(int pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public PagedPatientResponseDto build() {
            return new PagedPatientResponseDto(patients, totalPages, totalElements, pageNumber, pageSize);
        }
    }
}