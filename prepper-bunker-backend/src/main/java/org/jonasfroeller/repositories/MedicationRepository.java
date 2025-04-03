package org.jonasfroeller.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.jonasfroeller.models.Medication;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class MedicationRepository implements PanacheRepository<Medication> {
    // Find medications by location
    public List<Medication> findByStorageLocation(Long storageLocationId) {
        return find("storageLocation.id", storageLocationId).list();
    }

    // Find expired medications
    public List<Medication> findExpiredItems() {
        return find("expirationDate < ?1", LocalDate.now()).list();
    }

    // Find medications by purpose
    public List<Medication> findByPurpose(String purpose) {
        return find("purpose", purpose).list();
    }

    // Find medications expiring soon (within the next month)
    public List<Medication> findItemsExpiringSoon() {
        LocalDate now = LocalDate.now();
        LocalDate oneMonthFromNow = now.plusMonths(1);
        return find("expirationDate > ?1 and expirationDate < ?2",
                now, oneMonthFromNow).list();
    }
}