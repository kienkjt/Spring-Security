package com.kjt.springsecurity.controller;

import com.kjt.springsecurity.dto.UserInfo;
import com.kjt.springsecurity.entity.User;
import com.kjt.springsecurity.repository.UserRepository;
import com.kjt.springsecurity.service.UserService;
import com.kjt.springsecurity.util.APIResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserRepository userRepository;
    private final UserService userService;

    public UserController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }


    @GetMapping("/my-profile")
    public ResponseEntity<APIResponse<UserInfo>> getMyProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        UserInfo userInfo = userService.getUserInfo(currentUsername);
        return ResponseEntity.ok(APIResponse.success(userInfo, "Lấy thông tin profile thành công"));
    }
}
