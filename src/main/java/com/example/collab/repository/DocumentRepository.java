package com.example.collab.repository;

import com.example.collab.entity.Documents;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends MongoRepository<Documents, String> {
    List<Documents> findByOwnerIdOrCollaboratorsContains(String ownerId, String collaboratorId);
}
