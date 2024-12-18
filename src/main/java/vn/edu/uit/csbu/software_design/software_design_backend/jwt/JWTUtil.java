package vn.edu.uit.csbu.software_design.software_design_backend.jwt;

import java.util.Date;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

/**
 * The `JWTUtil` class provides methods to generate, validate, and extract information from JWT tokens
 * using a secret key for signing.
 */
public class JWTUtil {
    private static final long EXPIRATION_TIME = 3600000; // 1 day in milliseconds
    private static final SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    /**
     * The `generateToken` function creates a JWT token with the given ID, issued at the current time,
     * and set to expire after a specified duration.
     *
     * @param id The `id` parameter in the `generateToken` method represents the subject of the JWT (JSON Web Token). It is typically a unique identifier for the entity (user, client, etc.) for whom the token is being generated. This subject is usually used to identify the recipient of the token when
     * @return A JWT (JSON Web Token) is being returned by the `generateToken` method.
     */
    public static String generateToken(String id) {
        return Jwts.builder()
        .setSubject(id)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 1 hour
        .signWith(key)
        .compact();
    }


    /**
     * The function `validateToken` checks if a given token is valid by parsing its claims using a
     * specified key.
     *
     * @param token The `token` parameter in the `validateToken` method is a string that represents a JSON Web Token (JWT). This method attempts to parse and validate the JWT using the `Jwts` class from the io.jsonwebtoken library. If the token is successfully parsed and verified, the method returns
     * @return The `validateToken` method returns a boolean value - `true` if the token is successfully validated, and `false` if an exception occurs during the validation process.
     */
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


    /**
     * The function `extractName` extracts the subject from a JWT token after removing the "Bearer "
     * prefix.
     *
     * @param token The `token` parameter is a string that represents a JSON Web Token (JWT) which typically consists of three parts separated by dots: header, payload, and signature. In the `extractName` method, the token is expected to be a JWT where the subject claim is extracted from the payload part
     * @return The method `extractName` returns the subject extracted from a JWT token after parsing it.
     */
    public static String extractName(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token.substring(7))
                .getBody()
                .getSubject();
    }
}
