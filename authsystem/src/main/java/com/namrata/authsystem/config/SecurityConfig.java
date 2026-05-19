package com.namrata.authsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public JwtFilter jwtFilter() {
        return new JwtFilter();
    }

    @Bean
    public org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder passwordEncoder() {
        return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                // ❌ Disable CSRF (important for Postman + APIs)
                .csrf(csrf -> csrf.disable())

                // ❌ No session (JWT based)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth

                        // ✅ PUBLIC APIs (MOST IMPORTANT)
                        .requestMatchers("/api/login", "/api/register").permitAll()

                        // ✅ STATIC HTML FILES
                        .requestMatchers(
                                "/",
                                "/login.html",
                                "/register.html",
                                "/profile.html"
                        ).permitAll()

                        // ✅ STATIC RESOURCES
                        .requestMatchers(
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/webjars/**"
                        ).permitAll()

                        // ✅ H2 Console
                        .requestMatchers("/h2-console/**").permitAll()

                        // ✅ ADMIN ONLY
                        .requestMatchers("/api/users", "/api/make-admin/**", "/api/admin/**")
                        .hasRole("ADMIN")

                        // ✅ EVERYTHING ELSE
                        .anyRequest().authenticated()
                )

                // ✅ Allow H2 Console frames
                .headers(headers ->
                        headers.frameOptions(frame -> frame.disable())
                )

                // ✅ JWT Filter BEFORE authentication
                .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class)

                .build();
    }
}