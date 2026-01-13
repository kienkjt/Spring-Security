package com.kjt.springsecurity.service.Impl;

import com.kjt.springsecurity.dto.LoginDto;
import com.kjt.springsecurity.dto.RegistrationDto;
import com.kjt.springsecurity.entity.User;
import com.kjt.springsecurity.repository.UserRepository;
import com.kjt.springsecurity.service.AuthService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;

    public AuthServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User register(RegistrationDto registrationDto) {
        User user = new User();
        if(registrationDto.getUsername() == null || registrationDto.getPassword() == null){
            throw new IllegalArgumentException("Username hoặc password không được để trống");
        }
        if(registrationDto.getPassword().equals(registrationDto.getConfirmPassword())){
            Optional<User> existingUser = Optional.ofNullable(userRepository.findByUsername(registrationDto.getUsername()));
            if(existingUser.isPresent()){
                throw new IllegalArgumentException("Username đã tồn tại");
            }
        } else {
            throw new IllegalArgumentException("Password và Confirm Password không khớp");
        }
        user.setUsername(registrationDto.getUsername());
        user.setPassword(registrationDto.getPassword());
        userRepository.save(user);
        return user;
    }

    @Override
    public User login(LoginDto loginDto) {
        User user = userRepository.findByUsername(loginDto.getUsername());
        if(user == null){
            throw new IllegalArgumentException("Username hoặc password không đúng");
        }
        if(!user.getPassword().equals(loginDto.getPassword())){
            throw new IllegalArgumentException("Username hoặc password không đúng");
        } else {
            return user;
        }
    }
}
