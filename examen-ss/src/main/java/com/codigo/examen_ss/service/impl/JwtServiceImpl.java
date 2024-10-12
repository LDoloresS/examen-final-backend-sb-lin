package com.codigo.examen_ss.service.impl;

import com.codigo.examen_ss.aggregates.constants.Constants;
import com.codigo.examen_ss.service.JwtService;
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
public class JwtServiceImpl implements JwtService {
    @Value("${key.signature}")
    private String keySignature;

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(addClaim(userDetails))
                .claim("ROLE", userDetails.getAuthorities())
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 300000))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())
                && !isTokenExpired(token));
    }

    private Key getSignKey() {
        byte[] key = Decoders.BASE64.decode(keySignature);
        return Keys.hmacShaKeyFor(key);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSignKey()).build()
                .parseClaimsJws(token).getBody();
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResult) {
        final Claims claims = extractAllClaims(token);
        return claimResult.apply(claims);
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private Map<String, Object> addClaim(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(Constants.CLAVE_ACCOUNTNONLOCKED, userDetails.isAccountNonLocked());
        claims.put(Constants.CLAVE_ACCOUNTNONEXPIRED, userDetails.isAccountNonExpired());
        claims.put(Constants.CLAVE_CREDENTIALSNONEXPIRED, userDetails.isCredentialsNonExpired());
        claims.put(Constants.CLAVE_ENABLED, userDetails.isEnabled());
        return claims;
    }
}
