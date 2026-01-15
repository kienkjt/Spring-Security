package com.kjt.springsecurity.controller;

import com.kjt.springsecurity.dto.AuthResponse;
import com.kjt.springsecurity.dto.LoginDto;
import com.kjt.springsecurity.dto.RefreshTokenRequest;
import com.kjt.springsecurity.dto.RegistrationDto;
import com.kjt.springsecurity.service.AuthService;
import com.kjt.springsecurity.util.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<APIResponse> register(@RequestBody RegistrationDto registrationDto) {
        try {
            authService.register(registrationDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(APIResponse.success(null, "Đăng ký thành công!"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(APIResponse.createFailureResponse(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<APIResponse> login(@RequestBody LoginDto loginDto) {
        try {
            AuthResponse response = authService.login(loginDto);
            return ResponseEntity.ok(APIResponse.success(response, "Login successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(APIResponse.createFailureResponse("Login failed: " + e.getMessage()));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<APIResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        try {
            AuthResponse response = authService.refreshToken(request.getRefreshToken());
            return ResponseEntity.ok(APIResponse.success(response, "Token refreshed successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(APIResponse.createFailureResponse("Refresh token failed: " + e.getMessage()));
        }
    }

}
