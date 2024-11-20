package vn.edu.uit.csbu.software_design.software_design_backend.chat;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.springframework.lang.NonNull;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ChatHandler extends TextWebSocketHandler  {
    private final ConcurrentHashMap<String, CopyOnWriteArraySet<WebSocketSession>> rooms = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        String roomId = getRoomId(session);
        rooms.putIfAbsent(roomId, new CopyOnWriteArraySet<>());
        rooms.get(roomId).add(session);

        System.out.println("User connected to room: " + roomId);
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) throws Exception {
        String payload = message.getPayload();
        Message msg = objectMapper.readValue(payload, Message.class);
        System.out.println("Room " + msg.getRoomId() + " - " + msg.getUser() + ": " + msg.getMessage());
    
        String roomId = msg.getRoomId(); // Use roomId from the message
        for (WebSocketSession s : rooms.getOrDefault(roomId, new CopyOnWriteArraySet<>())) {
            if (s.isOpen()) {
                s.sendMessage(new TextMessage(objectMapper.writeValueAsString(msg)));
            }
        }
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull org.springframework.web.socket.CloseStatus status) throws Exception {
        String roomId = getRoomId(session);
        rooms.getOrDefault(roomId, new CopyOnWriteArraySet<>()).remove(session);
        if (rooms.get(roomId).isEmpty()) {
            rooms.remove(roomId);
        }

        System.out.println("User disconnected from room: " + roomId);
    }

    @SuppressWarnings("null")
    private String getRoomId(@NonNull WebSocketSession session) {
        // Assume the room ID is passed as a query parameter in the URL
        // For example: ws://server/chat?roomId=123
        return session.getUri().getQuery().split("=")[1];
    }
}
