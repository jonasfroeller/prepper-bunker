package org.jonasfroeller.models;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "storage_location")
public class StorageLocation extends PanacheEntity {
    public String name;
    public String description;
}
