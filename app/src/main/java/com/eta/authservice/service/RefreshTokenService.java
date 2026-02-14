package com.eta.authservice.service;

import com.eta.authservice.entities.RefreshToken;
import com.eta.authservice.entities.UserInfo;
import com.eta.authservice.repository.RefreshTokenRepository;
import com.eta.authservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Autowired
    private UserRepository userRepository; // Repository to fetch user info

    @Autowired
    private RefreshTokenRepository refreshTokenRepository; // Repository to manage refresh tokens

    public RefreshToken createRefreshToken(String username){
        UserInfo extractedUserInfo = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found")); // Fetch user by username

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString()) // Generate random token
                .expiryDate(Instant.now().plus(Duration.ofDays(7))) // Set 7-day expiry
                .userInfo(extractedUserInfo) // Associate token with user
                .build();

        return refreshTokenRepository.save(refreshToken); // Persist refresh token
    }

    public Optional<RefreshToken> findByToken(String token){
        return refreshTokenRepository.findByToken(token); // Retrieve refresh token by value
    }

    public RefreshToken verifyExpiration(RefreshToken token){
        if (token.getExpiryDate().isBefore(Instant.now())){
            refreshTokenRepository.delete(token); // Delete expired token
            throw new RuntimeException(token.getToken() + " Refresh token is expired. Please login again...!"); // Notify client
        }
        return token; // Return valid token
    }
}
