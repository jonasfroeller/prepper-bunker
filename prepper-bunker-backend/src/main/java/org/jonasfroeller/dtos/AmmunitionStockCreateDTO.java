package org.jonasfroeller.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AmmunitionStockCreateDTO {
    @NotNull(message = "Ammunition type ID is required")
    public Long ammunitionTypeId;

    @Min(value = 1, message = "Quantity must be at least 1")
    public int quantity;

    @NotNull(message = "Storage location ID is required")
    public Long storageLocationId;
}