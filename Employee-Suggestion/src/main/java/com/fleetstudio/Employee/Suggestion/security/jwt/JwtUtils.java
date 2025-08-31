package com.fleetstudio.Employee.Suggestion.security.jwt;

import com.fleetstudio.Employee.Suggestion.security.jwt.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtUtils {
    // Use a strong secret key (base64 encoded)
    private final String secret = "Km9Vc8UzL3Rnbk5vcEd1cGpQaXZ4V29VdDVmQ1lxaDA=";

    // Helper to get token from HTTP header
    public String getJwtFromHeader(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return null;
    }

    // Generate token using user details
    public String generateToken(UserDetailsImpl userDetails) {
        String email = userDetails.getEmail();
        String roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder().setSubject(email)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 36000000)) // 1 day
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Decode the secret key
    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    // Extract username (subject) from token
    public String getUserNameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Validate the token
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token); // Use ClaimsJws parser for validation

            return true;
        } catch (io.jsonwebtoken.security.SignatureException e) {
            System.out.println("❌ Invalid JWT signature");
        } catch (MalformedJwtException e) {
            System.out.println("❌ Malformed JWT token");
        } catch (ExpiredJwtException e) {
            System.out.println("❌ JWT token expired");
        } catch (UnsupportedJwtException e) {
            System.out.println("❌ Unsupported JWT token");
        } catch (IllegalArgumentException e) {
            System.out.println("❌ JWT token string is empty");
        }

        return false;
    }
}
