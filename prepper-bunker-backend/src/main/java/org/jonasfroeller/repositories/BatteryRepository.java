package org.jonasfroeller.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.jonasfroeller.models.Battery;

import java.util.List;

@ApplicationScoped
public class BatteryRepository implements PanacheRepository<Battery> {
    // Find batteries by storage location
    public List<Battery> findByStorageLocation(Long storageLocationId) {
        return find("storageLocation.id", storageLocationId).list();
    }

    // Find batteries by type
    public List<Battery> findByType(String type) {
        return find("type", type).list();
    }

    // Get total quantity of a specific battery type
    public int getTotalQuantityByType(String type) {
        return find("type", type)
                .stream()
                .mapToInt(battery -> battery.quantity)
                .sum();
    }
}