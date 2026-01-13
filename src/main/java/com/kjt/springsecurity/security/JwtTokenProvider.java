package com.kjt.springsecurity.security;

import com.kjt.springsecurity.service.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    @Value("${jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;


    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);
        String username = authentication.getName();
        // Tạo và trả về JWT từ thông tin người dùng
        return io.jsonwebtoken.Jwts.builder()
                .setSubject(username)
                .claim("roles", authentication.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), io.jsonwebtoken.SignatureAlgorithm.HS512)
                .compact();
    }
    public String generateRefreshToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshExpirationMs);

        return io.jsonwebtoken.Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), io.jsonwebtoken.SignatureAlgorithm.HS512)
                .compact();
    }

    public String getUsernameFromToken(String token){
        return io.jsonwebtoken.Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String getRoleFromToken(String token){
        return (String) io.jsonwebtoken.Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role");
    }

    public boolean validateToken(String token) {
        try {
            io.jsonwebtoken.Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException ex) {
            log.error("JWT token không hợp lệ");
        } catch (ExpiredJwtException ex) {
            log.error("JWT token đã hết hạn");
        } catch (UnsupportedJwtException ex) {
            log.error("JWT token không được hỗ trợ");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims trống");
        }
        return false;
    }
}
