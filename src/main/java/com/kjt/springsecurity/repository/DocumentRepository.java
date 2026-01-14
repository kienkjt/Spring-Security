package com.kjt.springsecurity.repository;

import com.kjt.springsecurity.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByOwnerIdAndIsDeletedFalse(Long ownerId);

    List<Document> findByDepartmentAndIsDeletedFalse(String department);

    List<Document> findByClassificationLevelLessThanEqualAndIsDeletedFalse(Integer classificationLevel);

    List<Document> findByIsDeletedFalse();
}
