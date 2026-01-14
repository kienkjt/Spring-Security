package com.kjt.springsecurity.service;

import com.kjt.springsecurity.entity.User;

import java.util.Optional;

public interface UserService {
    Optional<User> findByUsername(String username);
}
