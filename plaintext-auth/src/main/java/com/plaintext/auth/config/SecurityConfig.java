package com.plaintext.auth.config;

import com.plaintext.auth.security.JwtAuthenticationFilter;
import com.plaintext.common.security.CommonAuthEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CommonAuthEntryPoint authEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * The Security Filter Chain defines which requests are allowed.
     * This is the "Bouncer" at the door.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. Disable CSRF (Cross-Site Request Forgery)
                // standard for stateless APIs because we use JWTs, not cookies.
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Set Session Management to STATELESS
                // The server will NOT create a JSESSIONID. Every request must have a token.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 3. Define Access Rules
                .authorizeHttpRequests(auth -> auth
                        // Public Endpoints
                        .requestMatchers("/api/auth/signup", "/api/auth/login", "/api/auth/tnc").permitAll()
                        // Protected Endpoints (everything else)
                        .anyRequest().authenticated())

                // 4. Exception Handling
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authEntryPoint))

                // 5. Add JWT Filter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Password Encoder: BCrypt
     * We NEVER store plain text passwords. This bean handles the hashing.
     * Rounds: 10 (default) is a good balance between security and speed.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Authentication Manager
     * This is the component that actually processes "login" attempts.
     * It uses the PasswordEncoder to check if the entered password matches the
     * hash.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}