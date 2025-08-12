package com.example.collab.controller;

import com.example.collab.entity.ChatMessage;
import com.example.collab.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatMessageRepository chatRepo;

    @GetMapping("/{roomId}")
    public List<ChatMessage> getChatHistory(@PathVariable String roomId) {
        return chatRepo.findByRoomIdOrderByTimestampAsc(roomId);
    }
}
