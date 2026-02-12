package com.eta.authservice.controller;

import com.eta.authservice.entities.RefreshToken;
import com.eta.authservice.model.UserInfoDto;
import com.eta.authservice.response.JwtResponseDto;
import com.eta.authservice.service.JwtService;
import com.eta.authservice.service.RefreshTokenService;
import com.eta.authservice.service.UserDetailsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private JwtService jwtService;
    @Autowired
    private RefreshTokenService refreshTokenService;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @PostMapping("auth/v1/signup")
    public ResponseEntity<?> signUp(@RequestBody UserInfoDto userInfoDto){
        try{
            Boolean isSignedUp = userDetailsService.signUp(userInfoDto); // Attempt to register new user

            if (Boolean.FALSE.equals(isSignedUp)){
                log.warn("Signup failed: User {} already exists", userInfoDto.getUsername());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exists");
            }

            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userInfoDto.getUsername()); // Generate refresh token
            String jwtToken = jwtService.generateToken(userInfoDto.getUsername()); // Generate access token

            JwtResponseDto jwtResponseDto = JwtResponseDto.builder()
                    .accessToken(jwtToken)
                    .token(refreshToken.getToken())
                    .build(); // Build JWT response payload

            log.info("User {} signed up successfully", userInfoDto.getUsername());

            return ResponseEntity.ok(jwtResponseDto); // Return tokens to client
        } catch (Exception exception){
            log.error("Exception during user signup", exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error during signup"); // Handle unexpected errors
        }
    }
}
