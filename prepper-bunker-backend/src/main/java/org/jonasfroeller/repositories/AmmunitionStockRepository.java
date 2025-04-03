package org.jonasfroeller.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.jonasfroeller.models.AmmunitionStock;

import java.util.List;

@ApplicationScoped
public class AmmunitionStockRepository implements PanacheRepository<AmmunitionStock> {
    // Find ammunition stocks by location
    public List<AmmunitionStock> findByStorageLocation(Long storageLocationId) {
        return find("storageLocation.id", storageLocationId).list();
    }

    // Get total quantity of an ammunition type across all stocks
    public int getTotalQuantityByType(Long ammunitionTypeId) {
        return find("ammunitionType.id", ammunitionTypeId)
                .stream()
                .mapToInt(stock -> stock.quantity)
                .sum();
    }
}