package org.jonasfroeller.models;

import jakarta.persistence.*;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import java.time.LocalDate;

@Entity
public class Drink extends PanacheEntity {
    public String type;
    public double quantity;
    public LocalDate expirationDate;

    @ManyToOne
    @JoinColumn(name = "storage_location_id")
    public StorageLocation storageLocation;
}
