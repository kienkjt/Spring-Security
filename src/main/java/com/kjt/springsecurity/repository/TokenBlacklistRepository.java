package com.kjt.springsecurity.repository;

import com.kjt.springsecurity.entity.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, Long> {
    Optional<TokenBlacklist> findByToken(String token);

    boolean existsByToken(String token);

    @Modifying
    int deleteByExpiryDateBefore(Instant now);
}
