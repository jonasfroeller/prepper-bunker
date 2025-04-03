package org.jonasfroeller.models;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "drink")
public class Drink extends PanacheEntity {
    public String type;
    public double quantity;

    @Column(name = "expiration_date")
    public LocalDate expirationDate;

    @ManyToOne
    @JoinColumn(name = "storage_location_id")
    public StorageLocation storageLocation;
}
