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
    @Operation(summary = "Tạo tài liệu mới", description = "Tạo tài liệu do người dùng hiện tại sở hữu")
    public ResponseEntity<APIResponse<DocumentDto>> createDocument(@RequestBody DocumentDto dto) {
        try {
            DocumentDto created = documentService.createDocument(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(APIResponse.success(created, "Tài liệu được tạo thành công"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse.createFailureResponse(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy tài liệu theo ID", description = "Truy xuất tài liệu nếu chính sách ABAC cho phép")
    public ResponseEntity<APIResponse<DocumentDto>> getDocument(@PathVariable Long id) {
        try {
            DocumentDto document = documentService.getDocument(id);
            return ResponseEntity.ok(APIResponse.success(document, "Tài liệu được truy xuất thành công"));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(APIResponse.createFailureResponse("Access denied: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(APIResponse.createFailureResponse(e.getMessage()));
        }
    }

    @GetMapping("/all")
    @Operation(summary = "Lấy tất cả tài liệu có thể truy cập", description = "Trả về các tài liệu được lọc theo chính sách ABAC")
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
    @Operation(summary = "Cập nhật tài liệu", description = "Cập nhật tài liệu nếu chính sách ABAC cho phép")
    public ResponseEntity<APIResponse<DocumentDto>> updateDocument(@PathVariable Long id, @RequestBody DocumentDto dto) {
        try {
            DocumentDto updated = documentService.updateDocument(id, dto);
            return ResponseEntity.ok(APIResponse.success(updated, "Tài liệu được cập nhật thành công"));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(APIResponse.createFailureResponse("Access denied: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse.createFailureResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa tài liệu", description = "Xóa mềm tài liệu nếu chính sách ABAC cho phép")
    public ResponseEntity<APIResponse<Void>> deleteDocument(@PathVariable Long id) {
        try {
            documentService.deleteDocument(id);
            return ResponseEntity.ok(APIResponse.success(null, "Tài liệu được xóa thành công"));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(APIResponse.createFailureResponse("Access denied: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(APIResponse.createFailureResponse(e.getMessage()));
        }
    }
}
