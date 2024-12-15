package vn.edu.uit.csbu.software_design.software_design_backend.jwt;

import java.util.Date;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class JWTUtil {
    private static final long EXPIRATION_TIME = 3600000; // 1 day in milliseconds
    private static final SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Generate a JWT token
    public static String generateToken(String id) {
        return Jwts.builder()
        .setSubject(id)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 1 hour
        .signWith(key)
        .compact();
    }

    // Validate a JWT token
    public static boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            // Jwts.parser()
            //     .setSigningKey(SECRET_KEY)
            //     .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Extract username from the token
    public static String extractName(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token.substring(7))
                .getBody()
                .getSubject();
    }
}
