package org.jonasfroeller.models;

import jakarta.persistence.*;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class FuelType extends PanacheEntity {
    public String name;
}
