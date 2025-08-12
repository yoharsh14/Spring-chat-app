package com.example.collab.controller;

import com.example.collab.entity.Documents;
import com.example.collab.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    @Autowired
    private DocumentRepository documentRepository;

    // GET /api/documents/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Documents> getDocument(@PathVariable String id) {
        return documentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/documents
    @PostMapping
    public ResponseEntity<Documents> createDocument(@RequestBody Documents document) {
        document.setCreatedAt(LocalDateTime.now());
        document.setLastEditedAt(LocalDateTime.now());
        Documents savedDoc = documentRepository.save(document);
        return ResponseEntity.ok(savedDoc);
    }

    // PATCH /api/documents/{id}
    @PatchMapping("/{id}")
    public ResponseEntity<Documents> updateDocument(
            @PathVariable String id,
            @RequestBody Documents updatedContent
    ) {
        Optional<Documents> optionalDoc = documentRepository.findById(id);

        if (optionalDoc.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Documents existing = optionalDoc.get();
        existing.setContent(updatedContent.getContent());
        existing.setLastEditedAt(LocalDateTime.now());
        Documents saved = documentRepository.save(existing);

        return ResponseEntity.ok(saved);
    }

    // DELETE /api/documents/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable String id) {
        if (!documentRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        documentRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // (Optional) List all documents for a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Documents>> getUserDocuments(@PathVariable String userId) {
        List<Documents> docs = documentRepository.findByOwnerIdOrCollaboratorsContains(userId, userId);
        return ResponseEntity.ok(docs);
    }
}
