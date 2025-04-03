package org.jonasfroeller.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class BatteryCreateDTO {
    @NotBlank(message = "Battery type cannot be blank")
    public String type;

    @Min(value = 0, message = "Capacity cannot be negative")
    public double capacity;

    @Min(value = 1, message = "Quantity must be at least 1")
    public int quantity;

    @NotNull(message = "Storage location ID is required")
    public Long storageLocationId;
}