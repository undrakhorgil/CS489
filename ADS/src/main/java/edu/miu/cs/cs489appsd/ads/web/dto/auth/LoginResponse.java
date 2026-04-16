package edu.miu.cs.cs489appsd.ads.web.dto.auth;

public record LoginResponse(
        String tokenType,
        String accessToken,
        long expiresInSeconds,
        String username,
        String role
) {
}

