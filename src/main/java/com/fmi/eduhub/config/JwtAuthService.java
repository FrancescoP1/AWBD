package com.fmi.eduhub.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtAuthService {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Value("${jwt.accessTokenExpiry}")
    private Long ACCESS_TOKEN_EXPIRY;

    @Value("${jwt.refreshTokenExpiry}")
    private Long REFRESH_TOKEN_EXPIRY;

    public String generateJwtToken(UserDetails userDetails) {
        return generateJwtToken(new HashMap<>(), userDetails);
    }

    public String generateJwtToken(
            Map<String, Object> extraClaims, UserDetails userDetails) {
        long expirationTime = System.currentTimeMillis() +  ACCESS_TOKEN_EXPIRY;
        //System.out.println("UserName: " + userDetails.getUsername());
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(expirationTime))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(UserDetails userDetails) {
        long expirationTime = System.currentTimeMillis() + REFRESH_TOKEN_EXPIRY;
        return Jwts
            .builder()
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(expirationTime))
            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    public boolean isJwtTokenValid(String jwtToken, UserDetails userDetails) {
        final String username = extractUsername(jwtToken);
        return !isJwtTokenExpired(jwtToken) &&
                username.equals(userDetails.getUsername());
    }

    private Date extractExpiration(String jwtToken) {
        return extractClaim(jwtToken, Claims::getExpiration);
    }

    private boolean isJwtTokenExpired(String jwtToken) {
        return extractExpiration(jwtToken).before(new Date());
    }


    public String extractUsername(String jwtToken) {
        return extractClaim(jwtToken, Claims::getSubject);
    }

    public <T> T extractClaim(String jwtToken, Function<Claims, T> claimsResolver) {
        final Claims claims = extractClaims(jwtToken);
        return claimsResolver.apply(claims);
    }

    private Claims extractClaims(String jwtToken) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();
    }

    private Key getSignInKey() {
        // jwt signature
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
