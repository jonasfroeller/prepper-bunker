package org.jonasfroeller.websocket;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import org.jonasfroeller.dtos.ResourceUpdateMessage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/ws/updates")
@ApplicationScoped
public class ResourceUpdateSocket {
    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        sessions.put(session.getId(), session);
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        sessions.remove(session.getId());
    }

    public void broadcast(ResourceUpdateMessage message) {
        sessions.values().forEach(session -> {
            session.getAsyncRemote().sendObject(message, result -> {
                if (result.getException() != null) {
                    System.err.println("Unable to send message: " + result.getException());
                }
            });
        });
    }
}