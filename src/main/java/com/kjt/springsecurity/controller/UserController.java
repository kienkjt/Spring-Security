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

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserRepository userRepository;
    private final UserService userService;
    public UserController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }
    @GetMapping("/my-info")
    public ResponseEntity<APIResponse<UserInfo>> getMyInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User userInfo = userRepository.findByUsername(username);
        if (userInfo == null) {
            return ResponseEntity.status(404).body(APIResponse.createFailureResponse("User not found"));
        }
        UserInfo user = userService.getUserInfo(userInfo.getUsername());
        return ResponseEntity.ok(APIResponse.success(user, "Lấy thông tin user thành công"));
    }
}
