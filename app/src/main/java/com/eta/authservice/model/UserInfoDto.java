package com.eta.authservice.model;

import com.eta.authservice.entities.UserInfo;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class) // Use snake_case in JSON responses
public class UserInfoDto extends UserInfo {
    private String firstName; // User's first name
    private String lastName;  // User's last name
    private Long phoneNumber; // Contact number
    private String email;     // User email
}
