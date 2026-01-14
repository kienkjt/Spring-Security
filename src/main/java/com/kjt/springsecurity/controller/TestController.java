package com.kjt.springsecurity.controller;

import com.kjt.springsecurity.util.APIResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/public")
    public APIResponse<String> publicEndpoint() {
        return APIResponse.success("Public endpoint - không cần xác thực", "OK");
    }

    // Secured endpoint - cần đăng nhập
    @GetMapping("/secured")
    public APIResponse<String> securedEndpoint() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        List<String> authorities = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        String message = String.format("Secured endpoint - User: %s, Authorities: %s", username, authorities);
        return APIResponse.success(message, "Authenticated");
    }

    // ============ TEST ROLES ============

    @GetMapping("/user-role")
    @PreAuthorize("hasRole('USER')")
    public APIResponse<String> userRoleEndpoint() {
        return APIResponse.success("User endpoint - cần role USER", "OK");
    }

    @GetMapping("/admin-role")
    @PreAuthorize("hasRole('ADMIN')")
    public APIResponse<String> adminRoleEndpoint() {
        return APIResponse.success("Admin endpoint - cần role ADMIN", "OK");
    }

    @GetMapping("/moderator-role")
    @PreAuthorize("hasRole('MODERATOR')")
    public APIResponse<String> moderatorRoleEndpoint() {
        return APIResponse.success("Moderator endpoint - cần role MODERATOR", "OK");
    }

    // ============ TEST PERMISSIONS ============

    @GetMapping("/read-user")
    @PreAuthorize("hasAuthority('READ_USER')")
    public APIResponse<String> readUserPermission() {
        return APIResponse.success("Có quyền READ_USER", "OK");
    }

    @GetMapping("/create-user")
    @PreAuthorize("hasAuthority('CREATE_USER')")
    public APIResponse<String> createUserPermission() {
        return APIResponse.success("Có quyền CREATE_USER", "OK");
    }

    @GetMapping("/update-user")
    @PreAuthorize("hasAuthority('UPDATE_USER')")
    public APIResponse<String> updateUserPermission() {
        return APIResponse.success("Có quyền UPDATE_USER", "OK");
    }

    @GetMapping("/delete-user")
    @PreAuthorize("hasAuthority('DELETE_USER')")
    public APIResponse<String> deleteUserPermission() {
        return APIResponse.success("Có quyền DELETE_USER", "OK");
    }

    @GetMapping("/read-post")
    @PreAuthorize("hasAuthority('READ_POST')")
    public APIResponse<String> readPostPermission() {
        return APIResponse.success("Có quyền READ_POST", "OK");
    }

    @GetMapping("/create-post")
    @PreAuthorize("hasAuthority('CREATE_POST')")
    public APIResponse<String> createPostPermission() {
        return APIResponse.success("Có quyền CREATE_POST", "OK");
    }

    @GetMapping("/update-post")
    @PreAuthorize("hasAuthority('UPDATE_POST')")
    public APIResponse<String> updatePostPermission() {
        return APIResponse.success("Có quyền UPDATE_POST", "OK");
    }

    @GetMapping("/delete-post")
    @PreAuthorize("hasAuthority('DELETE_POST')")
    public APIResponse<String> deletePostPermission() {
        return APIResponse.success("Có quyền DELETE_POST", "OK");
    }

    // ============ TEST COMBINATIONS ============

    @GetMapping("/user-or-moderator")
    @PreAuthorize("hasAnyRole('USER', 'MODERATOR')")
    public APIResponse<String> userOrModerator() {
        return APIResponse.success("Cần role USER hoặc MODERATOR", "OK");
    }

    @GetMapping("/admin-only-create")
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('CREATE_USER')")
    public APIResponse<String> adminWithCreatePermission() {
        return APIResponse.success("Cần role ADMIN VÀ có quyền CREATE_USER", "OK");
    }

    @GetMapping("/post-operations")
    @PreAuthorize("hasAuthority('CREATE_POST') and hasAuthority('UPDATE_POST')")
    public APIResponse<String> postOperations() {
        return APIResponse.success("Cần cả CREATE_POST và UPDATE_POST", "OK");
    }

    // ============ TEST USER INFO ============

    @GetMapping("/my-info")
    public APIResponse<Object> getMyInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return APIResponse.success(auth, "User information");
    }

    @GetMapping("/check-roles-permissions")
    public APIResponse<Object> checkRolesPermissions() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        List<String> roles = auth.getAuthorities().stream()
                .filter(a -> a.getAuthority().startsWith("ROLE_"))
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .collect(Collectors.toList());

        List<String> permissions = auth.getAuthorities().stream()
                .filter(a -> !a.getAuthority().startsWith("ROLE_"))
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        var result = new Object() {
            public final String username = auth.getName();
            public final List<String> userRoles = roles;
            public final List<String> userPermissions = permissions;
            public final List<String> allAuthorities = auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
        };

        return APIResponse.success(result, "Roles and Permissions");
    }
}