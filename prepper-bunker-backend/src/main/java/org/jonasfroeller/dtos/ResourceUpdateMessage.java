package org.jonasfroeller.dtos;

public class ResourceUpdateMessage {
    public String action;  // "CREATE", "UPDATE", "DELETE"
    public String resourceType;  // "Weapon", "Food", etc.
    public Long resourceId;
    public String message;

    public ResourceUpdateMessage() {
    }

    public ResourceUpdateMessage(String action, String resourceType, Long resourceId, String message) {
        this.action = action;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.message = message;
    }
}