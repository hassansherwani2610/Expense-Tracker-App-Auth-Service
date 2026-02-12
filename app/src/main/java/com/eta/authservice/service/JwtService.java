package com.eta.authservice.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private final String SECRET_KEY;
    private final long jwtExpirationMs;

    public JwtService(@Value("${jwt.secret}") String secretKey, @Value("${jwt.expiration}") long jwtExpirationMs) {
        this.SECRET_KEY = Base64.getEncoder().encodeToString(secretKey.getBytes()); // Encode secret key in Base64
        this.jwtExpirationMs = jwtExpirationMs; // Token expiration in ms
    }

    private Key getSignKey(){
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes); // Get signing key for HMAC SHA256
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody(); // Parse and return JWT claims
        } catch (SecurityException | MalformedJwtException exception) {
            return null; // Token is tampered or invalid
        } catch (ExpiredJwtException expiredJwtException) {
            return null; // Token expired
        } catch (Exception exception) {
            return null; // Any other unexpected exception
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        Claims claims = extractAllClaims(token);
        if (claims == null) return null; // Invalid token
        return claimsResolver.apply(claims); // Extract specific claim
    }

    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject); // Get username from token
    }

    public Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration); // Get expiration date from token
    }

    private Boolean isTokenExpired(String token){
        Date expiration = extractExpiration(token);
        if (expiration == null) return true; // Treat null as expired/invalid
        return expiration.before(new Date()); // Check if token is expired
    }

    public Boolean validateToken(String token, UserDetails userDetails){
        String username = extractUsername(token);
        if (username == null) return false; // Invalid token
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token)); // Validate token
    }

    private String createToken(Map<String, Object> extraClaims, String username){
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs)) // Set expiration
                .signWith(getSignKey(), SignatureAlgorithm.HS256) // Sign with HMAC SHA256
                .compact(); // Build token
    }

    public String generateToken(String username){
        Map<String, Object> extraClaims = new HashMap<>();
        return createToken(extraClaims, username); // Generate JWT for user
    }
}
