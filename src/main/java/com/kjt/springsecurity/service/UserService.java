package com.kjt.springsecurity.service;

import com.kjt.springsecurity.dto.UserInfo;
import com.kjt.springsecurity.entity.User;

import java.util.Optional;

public interface UserService {
    Optional<User> findByUsername(String username);

    UserInfo getUserInfo(String username);
}
