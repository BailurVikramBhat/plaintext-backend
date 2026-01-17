package com.plaintext.auth.controller;

import com.plaintext.auth.dto.AuthResponse;
import com.plaintext.auth.dto.LoginRequest;
import com.plaintext.auth.dto.SignupRequest;
import com.plaintext.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/auth/signup
     * Creates a new user in the database.
     * 
     * @Valid ensures fields like @Email and @Size constraints are met before
     *        entering the method.
     */
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest request) {
        authService.registerUser(request);
        return ResponseEntity.ok("User registered successfully!");
    }

    /**
     * POST /api/auth/login
     * Authenticates the user and returns a JWT.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@Valid @RequestBody LoginRequest request) {
        // If authentication fails, Spring Security throws a 401 Unauthorized
        // automatically.
        return ResponseEntity.ok(authService.authenticateUser(request));
    }

    /**
     * POST /api/auth/tnc/accept
     * Records the user's acceptance of the current T&C version.
     */
    @PostMapping("/tnc/accept")
    public ResponseEntity<?> acceptTnc(org.springframework.security.core.Authentication authentication) {
        authService.acceptTnc(authentication.getName());
        return ResponseEntity.ok("Terms and Conditions accepted.");
    }
}