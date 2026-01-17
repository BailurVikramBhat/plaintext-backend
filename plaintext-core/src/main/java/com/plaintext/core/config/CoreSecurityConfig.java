package com.plaintext.core.config;

import com.plaintext.common.security.CommonAuthEntryPoint;
import com.plaintext.core.security.AuthTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class CoreSecurityConfig {

    private final AuthTokenFilter authTokenFilter;
    private final CommonAuthEntryPoint authEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. Enable CORS globally within Security
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // 2. Disable CSRF (we use JWTs)
                .csrf(AbstractHttpConfigurer::disable)
                // 3. Stateless Session
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 4. Route Protection
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/error").permitAll()
                        // All other API requests require a valid JWT
                        .anyRequest().authenticated())
                // 5. Exception Handling matches Auth Service
                .exceptionHandling(ex -> ex.authenticationEntryPoint(authEntryPoint))
                .addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Define the CORS rules.
     * This tells the browser: "It's okay to accept requests from localhost:5173"
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allow the React Frontend (Vite usually runs on 5173)
        // You can use "*" for development, but specific origin is safer
        configuration.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:3000"));

        // Allow standard HTTP methods
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Allow the Headers we send (Authorization for JWT, Content-Type for JSON)
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        // Allow credentials if needed (cookies), though we are using headers
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Apply this configuration to all endpoints in this service
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}