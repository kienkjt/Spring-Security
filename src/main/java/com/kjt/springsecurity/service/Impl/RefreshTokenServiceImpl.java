package com.kjt.springsecurity.service.Impl;

import com.kjt.springsecurity.entity.RefreshToken;
import com.kjt.springsecurity.entity.User;
import com.kjt.springsecurity.repository.RefreshTokenRepository;
import com.kjt.springsecurity.repository.UserRepository;
import com.kjt.springsecurity.security.JwtTokenProvider;
import com.kjt.springsecurity.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Value("${jwt.refresh-expiration-ms}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository,
            UserRepository userRepository,
            JwtTokenProvider jwtTokenProvider) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    @Transactional
    public RefreshToken createRefreshToken(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));

        // Generate unique token using JWT provider
        String token = jwtTokenProvider.generateRefreshToken(user.getUsername());
        refreshToken.setToken(token);

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token đã hết hạn. Vui lòng đăng nhập lại!");
        }
        return token;
    }

    @Override
    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Refresh token không tồn tại!"));
    }

    @Override
    @Transactional
    public void deleteByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        refreshTokenRepository.deleteByUser(user);
    }

    @Override
    @Transactional
    public void deleteByToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Refresh token không tồn tại!"));
        refreshTokenRepository.delete(refreshToken);
    }
}
