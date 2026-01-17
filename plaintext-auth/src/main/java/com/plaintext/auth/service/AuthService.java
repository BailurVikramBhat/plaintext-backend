package com.plaintext.auth.service;

import com.plaintext.auth.dto.AuthResponse;
import com.plaintext.auth.dto.LoginRequest;
import com.plaintext.auth.dto.SignupRequest;
import com.plaintext.auth.repository.UserRepository;
import com.plaintext.auth.util.JwtUtils;
import com.plaintext.common.enums.UserRole;
import com.plaintext.common.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    /**
     * REGISTER NEW USER
     * 1. Check if username/email exists.
     * 2. Hash the password.
     * 3. Save to DB.
     */
    public void registerUser(SignupRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new com.plaintext.common.exception.ConflictException("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new com.plaintext.common.exception.ConflictException("Error: Email is already in use!");
        }

        // Create new User entity
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword())) // CRITICAL: Hash password!
                .bio(request.getBio())
                .role(UserRole.USER) // Default role
                .build();

        userRepository.save(user);
    }

    /**
     * LOGIN USER
     * 1. Authenticate with Spring Security (checks password match).
     * 2. Generate JWT.
     * 3. Return Token + User Info.
     */
    public AuthResponse authenticateUser(LoginRequest request) {
        // 1. Authenticate
        // This line triggers the AuthenticationManager.
        // It will load the user from the DB and compare the hashed passwords.
        // If it fails, it throws a BadCredentialsException.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        // 2. Set Context (Optional for stateless, but good practice)
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Generate JWT
        String jwt = jwtUtils.generateToken(request.getUsername());

        // 4. Fetch User Details to return to client
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new com.plaintext.common.exception.ResourceNotFoundException("User not found"));

        return new AuthResponse(
                jwt,
                user.getUsername(),
                user.getEmail(),
                user.getRole().name());
    }
}