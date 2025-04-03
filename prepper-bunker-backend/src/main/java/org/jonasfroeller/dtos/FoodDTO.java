package org.jonasfroeller.dtos;

import java.time.LocalDate;

public class FoodDTO {
    public Long id;
    public String type;
    public double quantity;
    public LocalDate expirationDate;
    public Long storageLocationId;

    // Additional fields for related entities
    public String storageLocationName;

    public FoodDTO() {
    }

    public FoodDTO(Long id, String type, double quantity, LocalDate expirationDate, Long storageLocationId) {
        this.id = id;
        this.type = type;
        this.quantity = quantity;
        this.expirationDate = expirationDate;
        this.storageLocationId = storageLocationId;
    }

    public FoodDTO(Long id, String type, double quantity, LocalDate expirationDate,
                   Long storageLocationId, String storageLocationName) {
        this(id, type, quantity, expirationDate, storageLocationId);
        this.storageLocationName = storageLocationName;
    }
}