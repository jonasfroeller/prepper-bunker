package org.jonasfroeller.models;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "weapon")
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
