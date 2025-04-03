package org.jonasfroeller.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class WeaponCreateDTO {
    @NotBlank(message = "Weapon type cannot be blank")
    public String type;

    @NotBlank(message = "Weapon model cannot be blank")
    public String model;

    @Min(value = 1, message = "Quantity must be at least 1")
    public int quantity;

    @NotNull(message = "Ammunition type ID is required")
    public Long ammunitionTypeId;

    @NotNull(message = "Storage location ID is required")
    public Long storageLocationId;
}