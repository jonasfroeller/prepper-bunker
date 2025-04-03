package org.jonasfroeller.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class FuelCreateDTO {
    @NotNull(message = "Fuel type ID is required")
    public Long fuelTypeId;

    @Min(value = 0, message = "Quantity cannot be negative")
    public double quantity;

    @NotNull(message = "Storage location ID is required")
    public Long storageLocationId;
}