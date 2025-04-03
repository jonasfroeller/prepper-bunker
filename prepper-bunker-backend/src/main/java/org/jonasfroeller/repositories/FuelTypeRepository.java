package org.jonasfroeller.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.jonasfroeller.models.FuelType;

@ApplicationScoped
public class FuelTypeRepository implements PanacheRepository<FuelType> {
    // Basic CRUD operations are inherited from PanacheRepository
}