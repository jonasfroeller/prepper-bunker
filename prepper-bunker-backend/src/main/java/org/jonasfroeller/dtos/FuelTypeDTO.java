package org.jonasfroeller.dtos;

public class FuelTypeDTO {
    public Long id;
    public String name;

    public FuelTypeDTO() {
    }

    public FuelTypeDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}