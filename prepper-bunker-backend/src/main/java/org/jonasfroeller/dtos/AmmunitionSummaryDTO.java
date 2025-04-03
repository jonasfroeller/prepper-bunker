package org.jonasfroeller.dtos;

public class AmmunitionSummaryDTO {
    public Long ammunitionTypeId;
    public String caliber;
    public String type;
    public int totalQuantity;

    public AmmunitionSummaryDTO() {
    }

    public AmmunitionSummaryDTO(Long ammunitionTypeId, String caliber, String type, int totalQuantity) {
        this.ammunitionTypeId = ammunitionTypeId;
        this.caliber = caliber;
        this.type = type;
        this.totalQuantity = totalQuantity;
    }
}