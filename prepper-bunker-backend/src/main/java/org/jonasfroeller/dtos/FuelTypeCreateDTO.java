package org.jonasfroeller.dtos;

import jakarta.validation.constraints.NotBlank;

public class FuelTypeCreateDTO {
    @NotBlank(message = "Fuel type name cannot be blank")
    public String name;
}