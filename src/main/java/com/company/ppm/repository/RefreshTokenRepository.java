package com.company.ppm.repository;

import com.company.ppm.domain.entity.RefreshToken;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    @Modifying
    @Query("update RefreshToken t set t.revoked = true where t.user.id = :userId")
    void revokeAllByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("delete from RefreshToken t where t.expiresAt < :cutoff")
    void deleteExpired(@Param("cutoff") Instant cutoff);
}
