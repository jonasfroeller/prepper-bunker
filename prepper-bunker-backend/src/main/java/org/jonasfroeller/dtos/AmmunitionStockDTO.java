package org.jonasfroeller.dtos;

public class AmmunitionStockDTO {
    public Long id;
    public int quantity;
    public Long ammunitionTypeId;
    public Long storageLocationId;

    // Additional fields for related entities
    public String ammunitionTypeName;
    public String storageLocationName;

    public AmmunitionStockDTO() {
    }

    public AmmunitionStockDTO(Long id, int quantity, Long ammunitionTypeId, Long storageLocationId) {
        this.id = id;
        this.quantity = quantity;
        this.ammunitionTypeId = ammunitionTypeId;
        this.storageLocationId = storageLocationId;
    }

    public AmmunitionStockDTO(Long id, int quantity, Long ammunitionTypeId, Long storageLocationId,
                              String ammunitionTypeName, String storageLocationName) {
        this(id, quantity, ammunitionTypeId, storageLocationId);
        this.ammunitionTypeName = ammunitionTypeName;
        this.storageLocationName = storageLocationName;
    }
}