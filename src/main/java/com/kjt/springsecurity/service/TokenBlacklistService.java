package com.kjt.springsecurity.service;

public interface TokenBlacklistService {
    void blacklistToken(String token, String reason);

    boolean isTokenBlacklisted(String token);

    void cleanupExpiredTokens();
}
