package com.example.collab.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditMessage {
    private String documentId;
    private String userId;
    private String content; // the updated text or delta

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
