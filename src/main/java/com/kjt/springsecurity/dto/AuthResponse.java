package com.kjt.springsecurity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private Long userId;
    private String username;
    private String email;
    private Set<String> roles;
    private Set<String> permissions;

    public AuthResponse(String accessToken, Long userId, String username,
                        String email, Set<String> roles, Set<String> permissions) {
        this.accessToken = accessToken;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.permissions = permissions;
    }

}


