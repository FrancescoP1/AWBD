package com.fmi.eduhub.authentication.jwtToken;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JwtTokenRepository extends JpaRepository<JwtTokenEntity, Integer> {
    // method to find all non revoked and non expired tokens by userEmail
    Optional<JwtTokenEntity> findByUserEmail(String userEmail);
    Optional<JwtTokenEntity> findByJwtToken(String jwtToken);

    boolean existsByJwtTokenAndRefreshToken(String jwtToken, String refreshToken);

    // method to delete all expired and revoked tokens
    Integer deleteAllByExpiredTrueAndRevokedTrue();

    boolean existsByJwtTokenAndExpiredFalseAndRevokedFalse(String jwtToken);
}
