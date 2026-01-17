package com.plaintext.auth.service;

import com.plaintext.auth.dto.AuthResponse;
import com.plaintext.auth.dto.LoginRequest;
import com.plaintext.auth.dto.SignupRequest;
import com.plaintext.auth.repository.UserRepository;
import com.plaintext.auth.util.JwtUtils;
import com.plaintext.common.enums.UserRole;
import com.plaintext.common.exception.ConflictException;
import com.plaintext.common.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerUser_Success() {
        SignupRequest request = new SignupRequest("user", "user@test.com", "password", "bio");
        when(userRepository.existsByUsername("user")).thenReturn(false);
        when(userRepository.existsByEmail("user@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        authService.registerUser(request);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_UsernameTaken_ThrowsException() {
        SignupRequest request = new SignupRequest("user", "user@test.com", "password", "bio");
        when(userRepository.existsByUsername("user")).thenReturn(true);

        assertThrows(ConflictException.class, () -> authService.registerUser(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void authenticateUser_Success() {
        LoginRequest request = new LoginRequest("user", "password");
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtils.generateToken("user")).thenReturn("jwt-token");

        User user = User.builder()
                .username("user")
                .email("user@test.com")
                .role(UserRole.USER)
                .build();
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        AuthResponse response = authService.authenticateUser(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("user", response.getUsername());
    }

    @Test
    void authenticateUser_InvalidCredentials_ThrowsException() {
        LoginRequest request = new LoginRequest("user", "wrong-password");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> authService.authenticateUser(request));
    }
}
