package org.jonasfroeller.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.jonasfroeller.models.Drink;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class DrinkRepository implements PanacheRepository<Drink> {
    // Find drinks by location
    public List<Drink> findByStorageLocation(Long storageLocationId) {
        return find("storageLocation.id", storageLocationId).list();
    }

    // Find expired drinks
    public List<Drink> findExpiredItems() {
        return find("expirationDate < ?1", LocalDate.now()).list();
    }

    // Find drinks expiring soon (within the next month)
    public List<Drink> findItemsExpiringSoon() {
        LocalDate now = LocalDate.now();
        LocalDate oneMonthFromNow = now.plusMonths(1);
        return find("expirationDate > ?1 and expirationDate < ?2",
                now, oneMonthFromNow).list();
    }
}