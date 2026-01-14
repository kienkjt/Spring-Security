package com.kjt.springsecurity.service;

import com.kjt.springsecurity.dto.DocumentDto;

import java.util.List;

public interface DocumentService {
    DocumentDto createDocument(DocumentDto dto);

    DocumentDto getDocument(Long id);

    List<DocumentDto> getAllDocuments();

    DocumentDto updateDocument(Long id, DocumentDto dto);

    void deleteDocument(Long id);
}
