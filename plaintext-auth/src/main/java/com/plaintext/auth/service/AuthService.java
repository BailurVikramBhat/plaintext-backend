package com.plaintext.auth.service;

import com.plaintext.auth.dto.AuthResponse;
import com.plaintext.auth.dto.LoginRequest;
import com.plaintext.auth.dto.SignupRequest;
import com.plaintext.auth.repository.UserRepository;
import com.plaintext.auth.util.JwtUtils;
import com.plaintext.common.config.TncConfig;
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
                .lastAcceptedTncVersion(TncConfig.CURRENT_TNC_VERSION) // Implicit
                                                                       // acceptance
                .build();

        userRepository.save(user);
    }

    /**
     * LOGIN USER
     * 1. Authenticate with Spring Security (checks password match).
     * 2. Generate JWT.
     * 3. Return Token + User Info + T&C Status.
     */
    public AuthResponse authenticateUser(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateToken(request.getUsername());

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new com.plaintext.common.exception.ResourceNotFoundException("User not found"));

        // Check T&C Version
        boolean requiresTnc = !TncConfig.CURRENT_TNC_VERSION
                .equals(user.getLastAcceptedTncVersion());

        return new AuthResponse(
                jwt,
                user.getUsername(),
                user.getEmail(),
                user.getRole().name(),
                requiresTnc);
    }

    /**
     * UPDATE T&C STATUS
     */
    public void acceptTnc(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new com.plaintext.common.exception.ResourceNotFoundException("User not found"));

        user.setLastAcceptedTncVersion(TncConfig.CURRENT_TNC_VERSION);
        userRepository.save(user);
    }
}