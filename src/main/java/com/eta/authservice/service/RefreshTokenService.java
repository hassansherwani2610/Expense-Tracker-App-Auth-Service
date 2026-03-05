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

        UserInfo extractedUserInfo = userRepository.findByUsername(username); // Fetch user details by username

        Optional<RefreshToken> existingToken =
                refreshTokenRepository.findByUserInfo(extractedUserInfo); // Check if a refresh token already exists

        if (existingToken.isPresent()) {
            RefreshToken token = existingToken.get(); // Get the existing token
            token.setToken(UUID.randomUUID().toString()); // Generate new token string
            token.setExpiryDate(Instant.now().plus(Duration.ofDays(7))); // Extend expiry by 7 days
            token.setUserInfo(extractedUserInfo); // Link token to user
            return refreshTokenRepository.save(token); // Save updated token
        }

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString()) // Create token string
                .expiryDate(Instant.now().plus(Duration.ofDays(7))) // Set expiry date
                .userInfo(extractedUserInfo) // Link token to user
                .build();

        return refreshTokenRepository.save(refreshToken); // Save new token
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
