package com.daf.backend.security;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JWTService {
    private final SecretKey key;

    public JWTService(@Value("${pbm.jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * The methode generate, is used in the register and login, for creating a JWT Token that's used for validating user actions
     *
     * @param username Username is used for validation the user and is saved in JWT
     * @param role Role is used to check if a user can access a service or request
     *
     * @return JWT - JWT is used for validating every request (except methods: register, login)
     * */
    public String generate(String username, String role) {
        JwtBuilder jwtBuilder =  Jwts.builder()
                .subject(username)
                .claim("roles", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600*1000))
                .signWith(key);

        return jwtBuilder.compact();
    }

    /**
     * The methode extractUsername is for grabbing the username from the JWT Token. It is used for validating the user itself and for the integrity of JWT.
     *
     * @param token JWT-Token is needed to extract the username of it
     *
     * @return Username: The username is returned after successfully extracting it from the JWT
     * */
    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * The methode isValid checks the integrity and validates the JWT
     *
     * @param token JWT for validation
     *
     * @return is valid or is invalid
     * */
    public boolean isValid(String token) {
        try {
            extractUsername(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
