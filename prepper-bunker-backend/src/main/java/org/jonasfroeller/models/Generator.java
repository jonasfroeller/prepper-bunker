package org.jonasfroeller.models;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "generator")
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
