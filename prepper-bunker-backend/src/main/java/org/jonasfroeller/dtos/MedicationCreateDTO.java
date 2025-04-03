package org.jonasfroeller.dtos;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class MedicationCreateDTO {
    @NotBlank(message = "Medication name cannot be blank")
    public String name;

    @Min(value = 1, message = "Quantity must be at least 1")
    public int quantity;

    @NotNull(message = "Expiration date is required")
    @Future(message = "Expiration date must be in the future")
    public LocalDate expirationDate;

    @NotBlank(message = "Purpose cannot be blank")
    public String purpose;

    @NotNull(message = "Storage location ID is required")
    public Long storageLocationId;
}