package org.jonasfroeller.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.jonasfroeller.models.StorageLocation;

@ApplicationScoped
public class StorageLocationRepository implements PanacheRepository<StorageLocation> {
    // Basic CRUD operations are inherited from PanacheRepository
}