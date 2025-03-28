package org.jonasfroeller.model;

import jakarta.persistence.*;

@Entity
public class Weapon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
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
