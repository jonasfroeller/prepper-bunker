package org.jonasfroeller.dtos;

public class GeneratorDTO {
    public Long id;
    public String type;
    public double power;
    public String status;
    public Long fuelTypeId;
    public Long storageLocationId;

    // Additional fields for related entities
    public String fuelTypeName;
    public String storageLocationName;

    public GeneratorDTO() {
    }

    public GeneratorDTO(Long id, String type, double power, String status,
                        Long fuelTypeId, Long storageLocationId) {
        this.id = id;
        this.type = type;
        this.power = power;
        this.status = status;
        this.fuelTypeId = fuelTypeId;
        this.storageLocationId = storageLocationId;
    }

    public GeneratorDTO(Long id, String type, double power, String status,
                        Long fuelTypeId, Long storageLocationId,
                        String fuelTypeName, String storageLocationName) {
        this(id, type, power, status, fuelTypeId, storageLocationId);
        this.fuelTypeName = fuelTypeName;
        this.storageLocationName = storageLocationName;
    }
}