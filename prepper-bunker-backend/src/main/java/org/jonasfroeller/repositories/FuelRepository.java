package org.jonasfroeller.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.jonasfroeller.models.Fuel;

import java.util.List;

@ApplicationScoped
public class FuelRepository implements PanacheRepository<Fuel> {
    // Find fuel by storage location
    public List<Fuel> findByStorageLocation(Long storageLocationId) {
        return find("storageLocation.id", storageLocationId).list();
    }

    // Find fuel by type
    public List<Fuel> findByFuelType(Long fuelTypeId) {
        return find("fuelType.id", fuelTypeId).list();
    }

    // Get total quantity of a specific fuel type
    public double getTotalQuantityByType(Long fuelTypeId) {
        return find("fuelType.id", fuelTypeId)
                .stream()
                .mapToDouble(fuel -> fuel.quantity)
                .sum();
    }
}