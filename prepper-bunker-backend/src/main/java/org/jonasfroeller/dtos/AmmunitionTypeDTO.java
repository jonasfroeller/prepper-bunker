package org.jonasfroeller.dtos;

public class AmmunitionTypeDTO {
    public Long id;
    public String caliber;
    public String type;

    public AmmunitionTypeDTO() {
    }

    public AmmunitionTypeDTO(Long id, String caliber, String type) {
        this.id = id;
        this.caliber = caliber;
        this.type = type;
    }
}