package org.jonasfroeller.dtos;

public class WeaponDTO {
    public Long id;
    public String type;
    public String model;
    public int quantity;
    public Long ammunitionTypeId;
    public Long storageLocationId;

    // Additional fields for related entities
    public String ammunitionTypeName;
    public String storageLocationName;

    public WeaponDTO() {
    }

    public WeaponDTO(Long id, String type, String model, int quantity,
                     Long ammunitionTypeId, Long storageLocationId) {
        this.id = id;
        this.type = type;
        this.model = model;
        this.quantity = quantity;
        this.ammunitionTypeId = ammunitionTypeId;
        this.storageLocationId = storageLocationId;
    }

    public WeaponDTO(Long id, String type, String model, int quantity,
                     Long ammunitionTypeId, Long storageLocationId,
                     String ammunitionTypeName, String storageLocationName) {
        this(id, type, model, quantity, ammunitionTypeId, storageLocationId);
        this.ammunitionTypeName = ammunitionTypeName;
        this.storageLocationName = storageLocationName;
    }
}