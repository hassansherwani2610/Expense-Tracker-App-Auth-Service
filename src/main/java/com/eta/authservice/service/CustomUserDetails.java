package com.eta.authservice.service;

import com.eta.authservice.entities.UserInfo;
import com.eta.authservice.entities.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomUserDetails extends UserInfo implements UserDetails {

    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities; // Roles mapped to Spring Security authorities

    public CustomUserDetails(UserInfo byUsername){
        this.username = byUsername.getUsername(); // Copy username
        this.password = byUsername.getPassword(); // Copy password

        List<GrantedAuthority> auths = new ArrayList<>();

        for (UserRole role : byUsername.getRoles()){
            auths.add(new SimpleGrantedAuthority(role.getName().toUpperCase())); // Map roles to authorities
        }
        this.authorities = auths;
    }

    @Override
    public String getUsername(){
        return this.username; // Required by UserDetails
    }

    @Override
    public String getPassword(){
        return this.password; // Required by UserDetails
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities; // Return user roles as authorities
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Account never expires (customize if needed)
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Account never locked (customize if needed)
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Credentials never expire
    }

    @Override
    public boolean isEnabled() {
        return true; // Account always enabled
    }
}
