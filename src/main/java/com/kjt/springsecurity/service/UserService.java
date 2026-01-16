package com.kjt.springsecurity.service;

import com.kjt.springsecurity.dto.UserInfo;

public interface UserService {
    UserInfo getUserInfo(String username);
}
