package org.jonasfroeller.models;

import jakarta.persistence.*;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class AmmunitionType extends PanacheEntity {
    public String caliber;
    public String type;
}
