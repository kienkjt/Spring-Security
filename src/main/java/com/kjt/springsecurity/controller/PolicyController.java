package com.kjt.springsecurity.controller;

import com.kjt.springsecurity.dto.PolicyDto;
import com.kjt.springsecurity.service.PolicyService;
import com.kjt.springsecurity.util.APIResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/policies")
@Tag(name = "Policy Management", description = "ABAC policy administration")
public class PolicyController {

    private final PolicyService policyService;

    public PolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Tạo chính sách mới", description = "Chỉ dành cho Admin - Tạo chính sách ABAC mới")
    public ResponseEntity<APIResponse<PolicyDto>> createPolicy(@RequestBody PolicyDto dto) {
        try {
            PolicyDto created = policyService.createPolicy(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(APIResponse.success(created, "Policy created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse.createFailureResponse(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lấy chính sách theo ID", description = "Chỉ dành cho Admin - Truy xuất chính sách cụ thể")
    public ResponseEntity<APIResponse<PolicyDto>> getPolicy(@PathVariable Long id) {
        try {
            PolicyDto policy = policyService.getPolicy(id);
            return ResponseEntity.ok(APIResponse.success(policy, "Policy retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(APIResponse.createFailureResponse(e.getMessage()));
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lấy tất cả chính sách", description = "Chỉ dành cho Admin - Liệt kê tất cả các chính sách")
    public ResponseEntity<APIResponse<List<PolicyDto>>> getAllPolicies() {
        try {
            List<PolicyDto> policies = policyService.getAllPolicies();
            return ResponseEntity.ok(APIResponse.success(policies,
                    "Retrieved " + policies.size() + " policies"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(APIResponse.createFailureResponse(e.getMessage()));
        }
    }

    @GetMapping("/active")
    @Operation(summary = "Lấy các chính sách đang hoạt động", description = "Liệt kê tất cả các chính sách đang hoạt động")
    public ResponseEntity<APIResponse<List<PolicyDto>>> getActivePolicies() {
        try {
            List<PolicyDto> policies = policyService.getAllPolicies();
            return ResponseEntity.ok(APIResponse.success(policies,
                    "Retrieved " + policies.size() + " active policies"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(APIResponse.createFailureResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cập nhật chính sách", description = "Chỉ dành cho Admin - Cập nhật chính sách hiện có")
    public ResponseEntity<APIResponse<PolicyDto>> updatePolicy(@PathVariable Long id, @RequestBody PolicyDto dto) {
        try {
            PolicyDto updated = policyService.updatePolicy(id, dto);
            return ResponseEntity.ok(APIResponse.success(updated, "Policy updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse.createFailureResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Xóa chính sách", description = "Chỉ dành cho Admin - Xóa mềm một chính sách")
    public ResponseEntity<APIResponse<Void>> deletePolicy(@PathVariable Long id) {
        try {
            policyService.deletePolicy(id);
            return ResponseEntity.ok(APIResponse.success(null, "Policy deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(APIResponse.createFailureResponse(e.getMessage()));
        }
    }
}
