package com.kjt.springsecurity.controller;

import com.kjt.springsecurity.dto.DocumentDto;
import com.kjt.springsecurity.service.DocumentService;
import com.kjt.springsecurity.util.APIResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/documents")
@Tag(name = "Document Management", description = "ABAC-protected document operations")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping
    @Operation(summary = "Create a new document", description = "Creates a document owned by the current user")
    public ResponseEntity<APIResponse<DocumentDto>> createDocument(@RequestBody DocumentDto dto) {
        try {
            DocumentDto created = documentService.createDocument(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(APIResponse.success(created, "Document created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse.createFailureResponse(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get document by ID", description = "Retrieves a document if ABAC policies allow")
    public ResponseEntity<APIResponse<DocumentDto>> getDocument(@PathVariable Long id) {
        try {
            DocumentDto document = documentService.getDocument(id);
            return ResponseEntity.ok(APIResponse.success(document, "Document retrieved successfully"));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(APIResponse.createFailureResponse("Access denied: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(APIResponse.createFailureResponse(e.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "Get all accessible documents", description = "Returns documents filtered by ABAC policies")
    public ResponseEntity<APIResponse<List<DocumentDto>>> getAllDocuments() {
        try {
            List<DocumentDto> documents = documentService.getAllDocuments();
            return ResponseEntity.ok(APIResponse.success(documents,
                    "Retrieved " + documents.size() + " accessible documents"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(APIResponse.createFailureResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update document", description = "Updates a document if ABAC policies allow")
    public ResponseEntity<APIResponse<DocumentDto>> updateDocument(@PathVariable Long id,
            @RequestBody DocumentDto dto) {
        try {
            DocumentDto updated = documentService.updateDocument(id, dto);
            return ResponseEntity.ok(APIResponse.success(updated, "Document updated successfully"));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(APIResponse.createFailureResponse("Access denied: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse.createFailureResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete document", description = "Soft deletes a document if ABAC policies allow")
    public ResponseEntity<APIResponse<Void>> deleteDocument(@PathVariable Long id) {
        try {
            documentService.deleteDocument(id);
            return ResponseEntity.ok(APIResponse.success(null, "Document deleted successfully"));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(APIResponse.createFailureResponse("Access denied: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(APIResponse.createFailureResponse(e.getMessage()));
        }
    }
}
