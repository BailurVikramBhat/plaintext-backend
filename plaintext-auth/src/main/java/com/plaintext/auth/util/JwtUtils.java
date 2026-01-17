package com.plaintext.auth.util;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {


    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    /**
     * 3. key() Helper
     * Converts our Hex/Base64 secret string into a cryptographic SecretKey object.
     * The JJWT library needs this object to perform the actual signing.
     */
    private SecretKey key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    /**
     * 4. generateToken
     * Creates a new JWT for a specific username.
     * @param username The user's unique identifier
     * @return String The signed JWT (looks like eyJhbGciOi...)
     */
    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username) // The "Subject" is the person this token is for
                .issuedAt(new Date()) // When was it created?
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs)) // When does it die?
                .signWith(key()) // Sign it with our secret key so it can't be tampered with
                .compact(); // Build it into a String
    }

    /**
     * 5. getUserNameFromJwtToken
     * Extracts the username from the token payload.
     * This is used to identify WHO is making the request.
     */
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith(key()) // Verify the signature matches our key
                .build()
                .parseSignedClaims(token) // Parse the token
                .getPayload()
                .getSubject(); // Get the "Subject" field (which is the username)
    }

    /**
     * 6. validateJwtToken
     * Checks two things:
     * A. Is the signature valid? (Did WE create this token?)
     * B. Is the token expired?
     */
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith(key())
                    .build()
                    .parse(authToken); // If this line doesn't throw an error, the token is valid
            return true;
        } catch (JwtException e) {
            // In a real app, you might log this error
            // e.g., "Malformed JWT", "Expired JWT", "Unsupported JWT"
            System.err.println("Invalid JWT token: " + e.getMessage());
        }
        return false;
    }
}