package org.jonasfroeller.dtos;

public class BatteryDTO {
    public Long id;
    public String type;
    public double capacity;
    public int quantity;
    public Long storageLocationId;

    // Additional fields for related entities
    public String storageLocationName;

    public BatteryDTO() {
    }

    public BatteryDTO(Long id, String type, double capacity, int quantity, Long storageLocationId) {
        this.id = id;
        this.type = type;
        this.capacity = capacity;
        this.quantity = quantity;
        this.storageLocationId = storageLocationId;
    }

    public BatteryDTO(Long id, String type, double capacity, int quantity,
                      Long storageLocationId, String storageLocationName) {
        this(id, type, capacity, quantity, storageLocationId);
        this.storageLocationName = storageLocationName;
    }
}