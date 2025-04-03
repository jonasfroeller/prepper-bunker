package org.jonasfroeller.models;

import jakarta.persistence.*;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class Generator extends PanacheEntity {
    public String type;
    public double power;
    public String status;

    @ManyToOne
    @JoinColumn(name = "fuel_type_id")
    public FuelType fuelType;

    @ManyToOne
    @JoinColumn(name = "storage_location_id")
    public StorageLocation storageLocation;
}
