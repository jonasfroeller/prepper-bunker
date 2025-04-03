package org.jonasfroeller.models;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "fuel_type")
public class FuelType extends PanacheEntity {
    public String name;
}
