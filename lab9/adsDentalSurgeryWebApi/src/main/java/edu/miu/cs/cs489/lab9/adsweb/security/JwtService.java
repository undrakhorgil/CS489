package edu.miu.cs.cs489.lab9.adsweb.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {
    private final SecretKey signingKey;
    private final long expiresInSeconds;

    public JwtService(
            @Value("${ads.security.jwt.secret}") String secret,
            @Value("${ads.security.jwt.expires-in-seconds}") long expiresInSeconds
    ) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiresInSeconds = expiresInSeconds;
    }

    public long getExpiresInSeconds() {
        return expiresInSeconds;
    }

    public String generateToken(AdsUserDetails user) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(expiresInSeconds);

        Map<String, Object> claims = Map.of(
                "role", user.getRole().name()
        );

        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .claims(claims)
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

