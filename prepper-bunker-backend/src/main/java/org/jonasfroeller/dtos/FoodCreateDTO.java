package org.jonasfroeller.dtos;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class FoodCreateDTO {
    @NotBlank(message = "Food type cannot be blank")
    public String type;

    @Min(value = 0, message = "Quantity cannot be negative")
    public double quantity;

    @NotNull(message = "Expiration date is required")
    @Future(message = "Expiration date must be in the future")
    public LocalDate expirationDate;

    @NotNull(message = "Storage location ID is required")
    public Long storageLocationId;
}