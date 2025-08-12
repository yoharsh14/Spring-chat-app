package com.example.collab.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Users {
    @Id
    private String id;

    private String username;
    private String email;
    private String passwordHash;
    private LocalDateTime createdAt;
}
