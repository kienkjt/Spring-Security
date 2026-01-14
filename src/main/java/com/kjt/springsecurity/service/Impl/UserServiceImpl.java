package com.kjt.springsecurity.service.Impl;

import com.kjt.springsecurity.entity.User;
import com.kjt.springsecurity.repository.UserRepository;
import com.kjt.springsecurity.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(userRepository.findByUsername(username));
    }
}
