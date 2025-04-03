package org.jonasfroeller.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.jonasfroeller.models.AmmunitionType;

@ApplicationScoped
public class AmmunitionTypeRepository implements PanacheRepository<AmmunitionType> {
    // Basic CRUD operations are inherited from PanacheRepository
}