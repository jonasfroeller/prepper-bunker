package org.jonasfroeller.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jonasfroeller.dtos.ResourceUpdateMessage;
import org.jonasfroeller.websocket.ResourceUpdateSocket;

@ApplicationScoped
public class NotificationService {

    @Inject
    ResourceUpdateSocket resourceUpdateSocket;

    public void notifyResourceCreated(String resourceType, Long resourceId, String details) {
        String message = resourceType + " created: " + details;
        ResourceUpdateMessage updateMessage = new ResourceUpdateMessage("CREATE", resourceType, resourceId, message);
        resourceUpdateSocket.broadcast(updateMessage);
    }

    public void notifyResourceUpdated(String resourceType, Long resourceId, String details) {
        String message = resourceType + " updated: " + details;
        ResourceUpdateMessage updateMessage = new ResourceUpdateMessage("UPDATE", resourceType, resourceId, message);
        resourceUpdateSocket.broadcast(updateMessage);
    }

    public void notifyResourceDeleted(String resourceType, Long resourceId, String details) {
        String message = resourceType + " deleted: " + details;
        ResourceUpdateMessage updateMessage = new ResourceUpdateMessage("DELETE", resourceType, resourceId, message);
        resourceUpdateSocket.broadcast(updateMessage);
    }
}