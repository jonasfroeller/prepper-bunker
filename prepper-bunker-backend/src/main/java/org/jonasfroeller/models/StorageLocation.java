package org.jonasfroeller.models;

import jakarta.persistence.*;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class StorageLocation extends PanacheEntity {
    public String name;
    public String description;
}
