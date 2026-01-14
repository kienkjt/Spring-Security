package com.kjt.springsecurity.service.Impl;

import com.kjt.springsecurity.dto.DocumentDto;
import com.kjt.springsecurity.entity.Document;
import com.kjt.springsecurity.entity.User;
import com.kjt.springsecurity.enums.DocumentStatus;
import com.kjt.springsecurity.repository.DocumentRepository;
import com.kjt.springsecurity.repository.UserRepository;
import com.kjt.springsecurity.service.DocumentService;
import com.kjt.springsecurity.service.PolicyService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final PolicyService policyService;

    public DocumentServiceImpl(DocumentRepository documentRepository,
            UserRepository userRepository,
            PolicyService policyService) {
        this.documentRepository = documentRepository;
        this.userRepository = userRepository;
        this.policyService = policyService;
    }

    @Override
    @Transactional
    public DocumentDto createDocument(DocumentDto dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User owner = userRepository.findByUsername(username);

        if (owner == null) {
            throw new RuntimeException("User not found");
        }

        Document document = new Document();
        document.setTitle(dto.getTitle());
        document.setContent(dto.getContent());
        document.setOwner(owner);
        document.setDepartment(dto.getDepartment() != null ? dto.getDepartment() : owner.getDepartment());
        document.setClassificationLevel(dto.getClassificationLevel() != null ? dto.getClassificationLevel() : 1);
        document.setStatus(dto.getStatus() != null ? DocumentStatus.valueOf(dto.getStatus()) : DocumentStatus.DRAFT);

        Document saved = documentRepository.save(document);
        return toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentDto getDocument(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        if (document.getIsDeleted()) {
            throw new RuntimeException("Document not found");
        }

        // Check ABAC permissions
        if (!checkAccess(document, "READ")) {
            throw new AccessDeniedException("You don't have permission to read this document");
        }

        return toDto(document);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentDto> getAllDocuments() {
        List<Document> allDocuments = documentRepository.findByIsDeletedFalse();

        // Filter documents based on ABAC policies
        return allDocuments.stream()
                .filter(doc -> checkAccess(doc, "READ"))
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DocumentDto updateDocument(Long id, DocumentDto dto) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        if (document.getIsDeleted()) {
            throw new RuntimeException("Document not found");
        }

        // Check ABAC permissions
        if (!checkAccess(document, "WRITE")) {
            throw new AccessDeniedException("You don't have permission to update this document");
        }

        if (dto.getTitle() != null)
            document.setTitle(dto.getTitle());
        if (dto.getContent() != null)
            document.setContent(dto.getContent());
        if (dto.getDepartment() != null)
            document.setDepartment(dto.getDepartment());
        if (dto.getClassificationLevel() != null)
            document.setClassificationLevel(dto.getClassificationLevel());
        if (dto.getStatus() != null)
            document.setStatus(DocumentStatus.valueOf(dto.getStatus()));

        Document updated = documentRepository.save(document);
        return toDto(updated);
    }

    @Override
    @Transactional
    public void deleteDocument(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        if (document.getIsDeleted()) {
            throw new RuntimeException("Document not found");
        }

        // Check ABAC permissions
        if (!checkAccess(document, "DELETE")) {
            throw new AccessDeniedException("You don't have permission to delete this document");
        }

        document.setIsDeleted(true);
        documentRepository.save(document);
    }

    private boolean checkAccess(Document document, String action) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username);

        if (currentUser == null) {
            return false;
        }

        return policyService.checkAccess("DOCUMENT", action, currentUser.getId(), document.getId());
    }

    private DocumentDto toDto(Document document) {
        DocumentDto dto = new DocumentDto();
        dto.setId(document.getId());
        dto.setTitle(document.getTitle());
        dto.setContent(document.getContent());
        dto.setOwnerId(document.getOwner().getId());
        dto.setOwnerUsername(document.getOwner().getUsername());
        dto.setDepartment(document.getDepartment());
        dto.setClassificationLevel(document.getClassificationLevel());
        dto.setStatus(document.getStatus().name());
        return dto;
    }
}
