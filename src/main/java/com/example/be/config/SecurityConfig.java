package com.example.be.config;

import com.example.be.security.JwtRequestFilter;
import com.example.be.security.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF
                .csrf(csrf -> csrf.disable())

                // Enable CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Set session management to stateless
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Set permissions on endpoints
                .authorizeHttpRequests(auth -> {
                    // Public endpoints không cần đăng nhập
                    auth.requestMatchers("/api/auth/**").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/routes/**").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/route-schedules/**").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/trips/**").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/trip-seats/**").permitAll();
                    auth.requestMatchers("/api/users/**").permitAll();

                    // Customer endpoints
                    auth.requestMatchers("/api/customers/**").hasAnyRole("ADMIN", "CUSTOMER");
                    auth.requestMatchers("/api/invoices/customer/**").hasAnyRole("CUSTOMER");

                    // Driver/Assistant endpoints
                    auth.requestMatchers("/api/trips/**").hasAnyRole("ADMIN", "DRIVER", "ASSISTANT");

                    // Thêm endpoints của trip-scheduler cho ADMIN
                    auth.requestMatchers("/api/trip-scheduler/**").hasRole("ADMIN");

                    // Tất cả các request khác yêu cầu xác thực
                    // Admin có quyền truy cập tất cả API
                    auth.anyRequest().hasRole("ADMIN");
                })

                // Add JWT token filter
                .addFilterBefore(new JwtRequestFilter(jwtUtil),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5178",  // Customer website
                "http://localhost:5179"   // Admin website
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "content-type", "x-auth-token"));
        configuration.setExposedHeaders(Arrays.asList("x-auth-token"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}