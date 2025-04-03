package org.jonasfroeller.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.jonasfroeller.models.Weapon;

import java.util.List;

@ApplicationScoped
public class WeaponRepository implements PanacheRepository<Weapon> {
    public List<Weapon> findByStorageLocation(Long storageLocationId) {
        return find("storageLocation.id", storageLocationId).list();
    }

    public long totalQuantity() {
        return findAll().stream().mapToLong(weapon -> weapon.quantity).sum();
    }
}
