package com.kjt.springsecurity.service;

import com.kjt.springsecurity.entity.RefreshToken;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(Long userId);

    RefreshToken verifyExpiration(RefreshToken token);

    RefreshToken findByToken(String token);

    void deleteByUserId(Long userId);

    void deleteByToken(String token);
}
