package com.eta.authservice.service;

import com.eta.authservice.entities.UserInfo;
import com.eta.authservice.eventProducer.UserInfoProducer;
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
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository; // Repository to fetch/save user data
    private final PasswordEncoder passwordEncoder; // For password hashing

    private final UserInfoProducer userInfoProducer;

    // Constructor injection
    public UserDetailsServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, UserInfoProducer userInfoProducer) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userInfoProducer = userInfoProducer;
    }

    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Entering loadUserByUsername method..."); // Debug entry
        UserInfo user = userRepository.findByUsername(username); // Fetch user from DB
        if (user == null) {
            log.error("Username not found: {}", username); // Log if user not found
            throw new UsernameNotFoundException("User not found!");
        }
        log.info("User authenticated successfully: {}", username); // Log successful fetch
        return new CustomUserDetails(user); // Map to Spring Security UserDetails
    }

    private UserInfo checkIfUserAlreadyExist(UserInfoDto userInfoDto){
        return userRepository.findByUsername(userInfoDto.getUsername()); // Check if user exists
    }

    public Boolean signUp(UserInfoDto userInfoDto){
        ValidationUtil.validateUserAttributes(userInfoDto); // Validate user input

        if (Objects.nonNull(checkIfUserAlreadyExist(userInfoDto))){
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

        userInfoProducer.sendEventToKafka(userInfoDto); // sending user info event to User Service by KAFKA

        return true; // Signup successful
    }
}
