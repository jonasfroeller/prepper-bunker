package org.jonasfroeller.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.jonasfroeller.models.Generator;

import java.util.List;

@ApplicationScoped
public class GeneratorRepository implements PanacheRepository<Generator> {
    // Find generators by storage location
    public List<Generator> findByStorageLocation(Long storageLocationId) {
        return find("storageLocation.id", storageLocationId).list();
    }

    // Find generators by fuel type
    public List<Generator> findByFuelType(Long fuelTypeId) {
        return find("fuelType.id", fuelTypeId).list();
    }

    // Find generators by status
    public List<Generator> findByStatus(String status) {
        return find("status", status).list();
    }
}