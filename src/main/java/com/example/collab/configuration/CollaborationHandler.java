package com.example.collab.configuration;

import com.example.collab.entity.ChatMessage;
import com.example.collab.repository.ChatMessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CollaborationHandler extends TextWebSocketHandler {

    @Autowired
    ChatMessageRepository chatMessageRepository;

    // roomId -> set of client sessions
    private final Map<String, Set<WebSocketSession>> rooms = new ConcurrentHashMap<>();
    private final Map<WebSocketSession, String> sessionRoomMap = new ConcurrentHashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        System.out.println("Client connected: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Map<String, Object> payload = mapper.readValue(message.getPayload(), Map.class);

        String type = (String) payload.get("type");

        if ("join".equals(type)) {
            String documentId = (String) payload.get("documentId");
            rooms.computeIfAbsent(documentId, k -> ConcurrentHashMap.newKeySet()).add(session);
            sessionRoomMap.put(session, documentId);
            System.out.println("Session " + session.getId() + " joined room " + documentId);
        } else if ("chat".equalsIgnoreCase(type)) {
            handleChatMessage(session, payload);
        } else if ("edit".equals(type)) {
            String documentId = (String) payload.get("documentId");
            broadcastToRoom(documentId, message, session);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String roomId = sessionRoomMap.remove(session);
        if (roomId != null) {
            rooms.getOrDefault(roomId, Collections.emptySet()).remove(session);
        }
        System.out.println("Client disconnected: " + session.getId());
    }

    private void broadcastToRoom(String roomId, TextMessage message, WebSocketSession sender) throws Exception {
        Set<WebSocketSession> sessions = rooms.getOrDefault(roomId, Collections.emptySet());
        for (WebSocketSession s : sessions) {
            if (s.isOpen() && !s.getId().equals(sender.getId())) {
                s.sendMessage(message);
            }
        }
    }


    private void handleChatMessage(WebSocketSession session, Map<String, Object> payload) throws Exception {
        String roomId = (String) payload.get("roomId");
        String sender = (String) payload.get("sender");
        String messageText = (String) payload.get("message");

        // Save to MongoDB
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setRoomId(roomId);
        chatMessage.setSender(sender);
        chatMessage.setMessage(messageText);
        chatMessage.setTimestamp(Instant.now());
        chatMessageRepository.save(chatMessage);

        // Broadcast to other clients in the same room
        String json = mapper.writeValueAsString(payload);
        broadcastToRoom(roomId, new TextMessage(json), session);
    }

}
