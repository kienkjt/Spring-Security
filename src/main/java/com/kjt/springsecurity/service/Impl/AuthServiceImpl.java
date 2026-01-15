package com.kjt.springsecurity.service.Impl;

import com.kjt.springsecurity.dto.AuthResponse;
import com.kjt.springsecurity.dto.LoginDto;
import com.kjt.springsecurity.dto.RegistrationDto;
import com.kjt.springsecurity.entity.RefreshToken;
import com.kjt.springsecurity.entity.User;
import com.kjt.springsecurity.repository.UserRepository;
import com.kjt.springsecurity.security.JwtTokenProvider;
import com.kjt.springsecurity.service.AuthService;
import com.kjt.springsecurity.service.RefreshTokenService;
import com.kjt.springsecurity.service.TokenBlacklistService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {
        private final UserRepository userRepository;
        private final AuthenticationManager authenticationManager;
        private final JwtTokenProvider jwtTokenProvider;
        private final PasswordEncoder passwordEncoder;
        private final RefreshTokenService refreshTokenService;
        private final TokenBlacklistService tokenBlacklistService;

        public AuthServiceImpl(UserRepository userRepository, AuthenticationManager authenticationManager,
                        JwtTokenProvider jwtTokenProvider, PasswordEncoder passwordEncoder,
                        RefreshTokenService refreshTokenService, TokenBlacklistService tokenBlacklistService) {
                this.userRepository = userRepository;
                this.authenticationManager = authenticationManager;
                this.jwtTokenProvider = jwtTokenProvider;
                this.passwordEncoder = passwordEncoder;
                this.refreshTokenService = refreshTokenService;
                this.tokenBlacklistService = tokenBlacklistService;
        }

        @Override
        public void register(RegistrationDto registrationDto) {
                if (registrationDto.getUsername() == null || registrationDto.getUsername().trim().isEmpty()) {
                        throw new IllegalArgumentException("Username không được để trống");
                }
                if (registrationDto.getPassword() == null || registrationDto.getPassword().trim().isEmpty()) {
                        throw new IllegalArgumentException("Password không được để trống");
                }

                if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
                        throw new IllegalArgumentException("Password và Confirm Password không khớp");
                }

                User existingUser = userRepository.findByUsername(registrationDto.getUsername());
                if (existingUser != null) {
                        throw new IllegalArgumentException("Username đã tồn tại");
                }

                User user = new User();
                user.setUsername(registrationDto.getUsername());
                user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
                user.setEmail(registrationDto.getEmail());
                user.setIsActive(true);
                user.setIsDeleted(false);
                user.setCreatedAt(Instant.now());
                user.setUpdatedAt(Instant.now());

                userRepository.save(user);
        }

        @Override
        public AuthResponse login(LoginDto loginDto) {
                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(loginDto.getUsername(),
                                                loginDto.getPassword()));

                SecurityContextHolder.getContext().setAuthentication(authentication);

                String jwt = jwtTokenProvider.generateToken(authentication);

                // Extract user details
                User user = userRepository.findByUsername(loginDto.getUsername());
                if (user == null) {
                        throw new RuntimeException("User not found");
                }

                // Create refresh token
                RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

                Set<String> roles = authentication.getAuthorities().stream()
                                .map(auth -> auth.getAuthority())
                                .filter(a -> a.startsWith("ROLE_"))
                                .collect(Collectors.toSet());

                Set<String> permissions = authentication.getAuthorities().stream()
                                .map(auth -> auth.getAuthority())
                                .filter(a -> !a.startsWith("ROLE_"))
                                .collect(Collectors.toSet());

                return new AuthResponse(
                                jwt,
                                refreshToken.getToken(),
                                user.getId(),
                                user.getUsername(),
                                user.getEmail(),
                                roles,
                                permissions);
        }

        @Override
        public AuthResponse refreshToken(String refreshTokenStr) {
                RefreshToken refreshToken = refreshTokenService.findByToken(refreshTokenStr);
                refreshToken = refreshTokenService.verifyExpiration(refreshToken);

                User user = refreshToken.getUser();

                // REFRESH TOKEN ROTATION: Delete old refresh token
                refreshTokenService.deleteByToken(refreshTokenStr);

                // Create new refresh token
                RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user.getId());

                // Load user details for authentication
                org.springframework.security.core.userdetails.UserDetails userDetails = org.springframework.security.core.userdetails.User
                                .builder()
                                .username(user.getUsername())
                                .password(user.getPassword())
                                .authorities(user.getUserRoles().stream()
                                                .flatMap(ur -> {
                                                        var authorities = new java.util.ArrayList<org.springframework.security.core.GrantedAuthority>();
                                                        authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority(
                                                                        ur.getRole().getName()));
                                                        authorities.addAll(ur.getRole().getRolePermissions().stream()
                                                                        .map(rp -> new org.springframework.security.core.authority.SimpleGrantedAuthority(
                                                                                        rp.getPermission().getName()))
                                                                        .toList());
                                                        return authorities.stream();
                                                })
                                                .toList())
                                .build();

                Authentication authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                String newAccessToken = jwtTokenProvider.generateToken(authentication);

                Set<String> roles = authentication.getAuthorities().stream()
                                .map(auth -> auth.getAuthority())
                                .filter(a -> a.startsWith("ROLE_"))
                                .collect(Collectors.toSet());

                Set<String> permissions = authentication.getAuthorities().stream()
                                .map(auth -> auth.getAuthority())
                                .filter(a -> !a.startsWith("ROLE_"))
                                .collect(Collectors.toSet());

                return new AuthResponse(
                                newAccessToken,
                                newRefreshToken.getToken(),
                                user.getId(),
                                user.getUsername(),
                                user.getEmail(),
                                roles,
                                permissions);
        }

        @Override
        public void logout(String accessToken, String refreshToken) {
                // Blacklist the access token
                tokenBlacklistService.blacklistToken(accessToken, "User logout");

                // Delete the refresh token
                if (refreshToken != null && !refreshToken.isEmpty()) {
                        try {
                                refreshTokenService.deleteByToken(refreshToken);
                        } catch (RuntimeException e) {
                                // Refresh token might already be deleted or invalid, ignore
                        }
                }
        }

        @Override
        public void logoutAllDevices(String accessToken) {
                // Blacklist the current access token
                tokenBlacklistService.blacklistToken(accessToken, "User logout from all devices");

                // Get username from token
                String username = jwtTokenProvider.getUsernameFromToken(accessToken);
                User user = userRepository.findByUsername(username);
                if (user != null) {
                        // Delete all refresh tokens for this user
                        refreshTokenService.deleteByUserId(user.getId());
                }
        }
}