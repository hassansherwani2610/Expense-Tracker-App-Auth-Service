package com.eta.authservice.repository;

import com.eta.authservice.entities.RefreshToken;
import com.eta.authservice.entities.UserInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token); // Fetch refresh token using its token string value

    Optional<RefreshToken> findByUserInfo(UserInfo userInfo); // Used to check if a user already has a refresh token, Prevents inserting duplicate rows when user_id has UNIQUE constraint.
}
