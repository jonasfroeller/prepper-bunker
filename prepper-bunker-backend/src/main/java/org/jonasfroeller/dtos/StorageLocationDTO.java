package org.jonasfroeller.dtos;

public class StorageLocationDTO {
    public Long id;
    public String name;
    public String description;

    public StorageLocationDTO() {
    }

    public StorageLocationDTO(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}