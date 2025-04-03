package org.jonasfroeller.models;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "medication")
public class Medication extends PanacheEntity {
    public String name;
    public int quantity;

    @Column(name = "expiration_date")
    public LocalDate expirationDate;

    public String purpose;

    @ManyToOne
    @JoinColumn(name = "storage_location_id")
    public StorageLocation storageLocation;
}
