package com.kjt.springsecurity.service;

import com.kjt.springsecurity.dto.AuthResponse;
import com.kjt.springsecurity.dto.LoginDto;
import com.kjt.springsecurity.dto.RegistrationDto;

public interface AuthService {
    void register(RegistrationDto registrationDto);

    AuthResponse login(LoginDto loginDto);

    AuthResponse refreshToken(String refreshToken);

    void logout(String accessToken, String refreshToken);

    void logoutAllDevices(String accessToken);
}
