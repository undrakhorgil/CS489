package edu.miu.cs.cs489.lab9.adsweb.web.dto.auth;

public record LoginResponse(
        String tokenType,
        String accessToken,
        long expiresInSeconds,
        String username,
        String role
) {
}

