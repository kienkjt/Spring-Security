package com.kjt.springsecurity.service.Impl;

import com.kjt.springsecurity.entity.TokenBlacklist;
import com.kjt.springsecurity.repository.TokenBlacklistRepository;
import com.kjt.springsecurity.security.JwtTokenProvider;
import com.kjt.springsecurity.service.TokenBlacklistService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
public class TokenBlacklistServiceImpl implements TokenBlacklistService {

    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public TokenBlacklistServiceImpl(TokenBlacklistRepository tokenBlacklistRepository,
            JwtTokenProvider jwtTokenProvider) {
        this.tokenBlacklistRepository = tokenBlacklistRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    @Transactional
    public void blacklistToken(String token, String reason) {
        // Check if token is already blacklisted
        if (tokenBlacklistRepository.existsByToken(token)) {
            log.warn("Token already blacklisted: {}", token.substring(0, Math.min(20, token.length())));
            return;
        }

        try {
            // Extract expiry date from token
            String username = jwtTokenProvider.getUsernameFromToken(token);

            TokenBlacklist blacklist = new TokenBlacklist();
            blacklist.setToken(token);
            blacklist.setReason(reason);

            // Set expiry date from token (we'll parse it manually to get expiration)
            // For now, we'll set a reasonable expiry time
            blacklist.setExpiryDate(Instant.now().plusSeconds(3600)); // 1 hour default

            tokenBlacklistRepository.save(blacklist);
            log.info("Token blacklisted for user: {}", username);
        } catch (Exception e) {
            log.error("Error blacklisting token: {}", e.getMessage());
            throw new RuntimeException("Failed to blacklist token");
        }
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        return tokenBlacklistRepository.existsByToken(token);
    }

    @Override
    @Transactional
    public void cleanupExpiredTokens() {
        int deleted = tokenBlacklistRepository.deleteByExpiryDateBefore(Instant.now());
        if (deleted > 0) {
            log.info("Cleaned up {} expired blacklisted tokens", deleted);
        }
    }
}
