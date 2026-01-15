package com.kjt.springsecurity.service.Impl;

import com.kjt.springsecurity.dto.UserInfo;
import com.kjt.springsecurity.entity.User;
import com.kjt.springsecurity.repository.UserRepository;
import com.kjt.springsecurity.service.UserService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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

    @Override
    public UserInfo getUserInfo(String username) {
        User user = userRepository.findByUsernameWithRolesAndPermissions(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        UserInfo userInfo = new UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setEmail(user.getEmail());
        userInfo.setDepartment(user.getDepartment());
        userInfo.setPosition(user.getPosition());
        return userInfo;
    }
}
