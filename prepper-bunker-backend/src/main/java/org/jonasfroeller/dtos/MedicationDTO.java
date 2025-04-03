package org.jonasfroeller.dtos;

import java.time.LocalDate;

public class MedicationDTO {
    public Long id;
    public String name;
    public int quantity;
    public LocalDate expirationDate;
    public String purpose;
    public Long storageLocationId;

    // Additional fields for related entities
    public String storageLocationName;

    public MedicationDTO() {
    }

    public MedicationDTO(Long id, String name, int quantity, LocalDate expirationDate,
                         String purpose, Long storageLocationId) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.expirationDate = expirationDate;
        this.purpose = purpose;
        this.storageLocationId = storageLocationId;
    }

    public MedicationDTO(Long id, String name, int quantity, LocalDate expirationDate,
                         String purpose, Long storageLocationId, String storageLocationName) {
        this(id, name, quantity, expirationDate, purpose, storageLocationId);
        this.storageLocationName = storageLocationName;
    }
}