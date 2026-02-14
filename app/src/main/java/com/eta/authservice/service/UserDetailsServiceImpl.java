package com.eta.authservice.service;

import com.eta.authservice.entities.UserInfo;
import com.eta.authservice.model.UserInfoDto;
import com.eta.authservice.repository.UserRepository;
import com.eta.authservice.utils.ValidationUtil;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository; // Repository to fetch/save user data
    private final PasswordEncoder passwordEncoder; // For password hashing

    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Entering loadUserByUsername method..."); // Debug entry
        UserInfo user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found")); // Fetch user from DB
        if (user == null) {
            log.error("Username not found: {}", username); // Log if user not found
            throw new UsernameNotFoundException("User not found!");
        }
        log.info("User authenticated successfully: {}", username); // Log successful fetch
        return new CustomUserDetails(user); // Map to Spring Security UserDetails
    }

    private Optional<UserInfo> checkIfUserAlreadyExist(UserInfoDto userInfoDto){
        return Optional.ofNullable(userRepository.findByUsername(userInfoDto.getUsername()).orElseThrow(() -> new UsernameNotFoundException("User not found"))); // Check if user exists
    }

    public Boolean signUp(UserInfoDto userInfoDto){
        ValidationUtil.validateUserAttributes(userInfoDto); // Validate user input

        if (checkIfUserAlreadyExist(userInfoDto).isPresent()){
            log.warn("User already exists: {}", userInfoDto.getUsername()); // Warn if duplicate user
            return false;
        }

        String userId = UUID.randomUUID().toString(); // Generate unique user ID
        userInfoDto.setPassword(passwordEncoder.encode(userInfoDto.getPassword())); // Hash password

        UserInfo userInfo = UserInfo.builder()
                .userId(userId)
                .username(userInfoDto.getUsername())
                .password(userInfoDto.getPassword())
                .roles(new HashSet<>()) // Initialize empty roles
                .build();

        userRepository.save(userInfo); // Persist new user
        log.info("New user signed up successfully: {}", userInfoDto.getUsername()); // Log signup success

        return true; // Signup successful
    }
}
