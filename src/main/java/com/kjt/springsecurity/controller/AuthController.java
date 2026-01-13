package com.kjt.springsecurity.controller;

import com.kjt.springsecurity.dto.RegistrationDto;
import com.kjt.springsecurity.entity.User;
import com.kjt.springsecurity.repository.UserRepository;
import com.kjt.springsecurity.service.AuthService;
import com.kjt.springsecurity.util.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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

}
