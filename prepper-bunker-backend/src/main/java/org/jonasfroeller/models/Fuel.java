package org.jonasfroeller.models;

import jakarta.persistence.*;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class Fuel extends PanacheEntity {
    public double quantity;

    @ManyToOne
    @JoinColumn(name = "fuel_type_id")
    public FuelType fuelType;

    @ManyToOne
    @JoinColumn(name = "storage_location_id")
    public StorageLocation storageLocation;
}
