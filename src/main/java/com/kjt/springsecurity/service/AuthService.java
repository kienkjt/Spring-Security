package com.kjt.springsecurity.service;

import com.kjt.springsecurity.dto.LoginDto;
import com.kjt.springsecurity.dto.RegistrationDto;
import com.kjt.springsecurity.entity.User;

public interface AuthService {
    User register(RegistrationDto registrationDto);
    User login(LoginDto loginDto);
}
