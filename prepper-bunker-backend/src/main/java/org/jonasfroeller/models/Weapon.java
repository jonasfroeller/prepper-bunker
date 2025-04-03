package org.jonasfroeller.models;

import jakarta.persistence.*;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class Weapon extends PanacheEntity {
    public String type;
    public String model;
    public int quantity;

    @ManyToOne
    @JoinColumn(name = "ammunition_type_id")
    public AmmunitionType ammunitionType;

    @ManyToOne
    @JoinColumn(name = "storage_location_id")
    public StorageLocation storageLocation;
}
