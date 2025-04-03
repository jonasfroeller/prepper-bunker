package org.jonasfroeller.models;

import jakarta.persistence.*;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class Battery extends PanacheEntity {
    public String type;
    public double capacity;
    public int quantity;

    @ManyToOne
    @JoinColumn(name = "storage_location_id")
    public StorageLocation storageLocation;
}
