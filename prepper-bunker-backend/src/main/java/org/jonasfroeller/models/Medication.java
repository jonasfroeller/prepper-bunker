package org.jonasfroeller.models;

import jakarta.persistence.*;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import java.time.LocalDate;

@Entity
public class Medication extends PanacheEntity {
    public String name;
    public int quantity;
    public LocalDate expirationDate;
    public String purpose;

    @ManyToOne
    @JoinColumn(name = "storage_location_id")
    public StorageLocation storageLocation;
}
