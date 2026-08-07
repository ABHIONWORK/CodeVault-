package com.example.codevault_backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF since REST APIs use stateless JWT tokens
            .csrf(csrf -> csrf.disable())
            
            // Define access rules for endpoints
            .authorizeHttpRequests(auth -> auth
                // Anyone can visit the login endpoint
                .requestMatchers("/api/auth/**").permitAll()
                // Anyone can view code snippets (GET requests)
                .requestMatchers(HttpMethod.GET, "/api/snippets/**").permitAll()
                // Only logged-in users with a valid JWT token can create or delete snippets
                .requestMatchers(HttpMethod.POST, "/api/snippets").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/snippets/**").authenticated()
                .anyRequest().authenticated()
            )
            
            // Use Stateless sessions (no server session storage)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Insert our JwtRequestFilter bouncer assistant before Spring's authentication filter
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
