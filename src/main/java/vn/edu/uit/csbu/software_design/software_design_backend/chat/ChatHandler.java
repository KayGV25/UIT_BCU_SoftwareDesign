package vn.edu.uit.csbu.software_design.software_design_backend.chat;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class ChatHandler extends TextWebSocketHandler  {
    private final ConcurrentHashMap<String, CopyOnWriteArraySet<WebSocketSession>> rooms = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String roomId = getRoomId(session);
        rooms.putIfAbsent(roomId, new CopyOnWriteArraySet<>());
        rooms.get(roomId).add(session);

        System.out.println("User connected to room: " + roomId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String roomId = getRoomId(session);
        String payload = message.getPayload();
        System.out.println("Message in room " + roomId + ": " + payload);

        // Broadcast to all clients in the same room
        for (WebSocketSession s : rooms.getOrDefault(roomId, new CopyOnWriteArraySet<>())) {
            if (s.isOpen()) {
                s.sendMessage(new TextMessage(payload));
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        String roomId = getRoomId(session);
        rooms.getOrDefault(roomId, new CopyOnWriteArraySet<>()).remove(session);
        if (rooms.get(roomId).isEmpty()) {
            rooms.remove(roomId);
        }

        System.out.println("User disconnected from room: " + roomId);
    }

    private String getRoomId(WebSocketSession session) {
        // Assume the room ID is passed as a query parameter in the URL
        // For example: ws://server/chat?roomId=123
        return session.getUri().getQuery().split("=")[1];
    }
}