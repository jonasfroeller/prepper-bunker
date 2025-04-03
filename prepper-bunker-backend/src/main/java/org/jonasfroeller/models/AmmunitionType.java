package org.jonasfroeller.models;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "ammunition_type")
public class AmmunitionType extends PanacheEntity {
    public String caliber;
    public String type;
}
