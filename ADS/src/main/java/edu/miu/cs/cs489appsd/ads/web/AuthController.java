package edu.miu.cs.cs489appsd.ads.web;

import edu.miu.cs.cs489appsd.ads.security.AdsUserDetails;
import edu.miu.cs.cs489appsd.ads.security.JwtService;
import edu.miu.cs.cs489appsd.ads.web.dto.auth.LoginRequest;
import edu.miu.cs.cs489appsd.ads.web.dto.auth.LoginResponse;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password())
        );

        AdsUserDetails user = (AdsUserDetails) auth.getPrincipal();
        String token = jwtService.generateToken(user);

        return new LoginResponse(
                "Bearer",
                token,
                jwtService.getExpiresInSeconds(),
                user.getUsername(),
                user.getRole().name()
        );
    }
}

