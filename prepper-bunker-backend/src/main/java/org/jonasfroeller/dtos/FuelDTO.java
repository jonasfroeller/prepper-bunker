package org.jonasfroeller.dtos;

public class FuelDTO {
    public Long id;
    public double quantity;
    public Long fuelTypeId;
    public Long storageLocationId;

    // Additional fields for related entities
    public String fuelTypeName;
    public String storageLocationName;

    public FuelDTO() {
    }

    public FuelDTO(Long id, double quantity, Long fuelTypeId, Long storageLocationId) {
        this.id = id;
        this.quantity = quantity;
        this.fuelTypeId = fuelTypeId;
        this.storageLocationId = storageLocationId;
    }

    public FuelDTO(Long id, double quantity, Long fuelTypeId, Long storageLocationId,
                   String fuelTypeName, String storageLocationName) {
        this(id, quantity, fuelTypeId, storageLocationId);
        this.fuelTypeName = fuelTypeName;
        this.storageLocationName = storageLocationName;
    }
}