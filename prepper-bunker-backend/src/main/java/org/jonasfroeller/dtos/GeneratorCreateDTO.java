package org.jonasfroeller.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class GeneratorCreateDTO {
    @NotBlank(message = "Generator type cannot be blank")
    public String type;

    @Min(value = 0, message = "Power cannot be negative")
    public double power;

    @NotNull(message = "Fuel type ID is required")
    public Long fuelTypeId;

    @NotBlank(message = "Status cannot be blank")
    public String status;

    @NotNull(message = "Storage location ID is required")
    public Long storageLocationId;
}