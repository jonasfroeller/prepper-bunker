package org.jonasfroeller.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.jonasfroeller.models.Food;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class FoodRepository implements PanacheRepository<Food> {
    // Find food by location
    public List<Food> findByStorageLocation(Long storageLocationId) {
        return find("storageLocation.id", storageLocationId).list();
    }

    // Find expired food
    public List<Food> findExpiredItems() {
        return find("expirationDate < ?1", LocalDate.now()).list();
    }

    // Find food expiring soon (within the next month)
    public List<Food> findItemsExpiringSoon() {
        LocalDate now = LocalDate.now();
        LocalDate oneMonthFromNow = now.plusMonths(1);
        return find("expirationDate > ?1 and expirationDate < ?2",
                now, oneMonthFromNow).list();
    }
}