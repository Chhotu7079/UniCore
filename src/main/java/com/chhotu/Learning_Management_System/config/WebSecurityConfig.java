
package com.chhotu.Learning_Management_System.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * WebSecurityConfig defines the Spring Security rules for the LMS application.
 * It allows public access to specific endpoints like authentication, Swagger docs, and APIs.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    /**
     * Configures HTTP security including public routes, CORS, CSRF, and logout settings.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 🔓 Define public endpoints (no authentication required)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/**",
                                "/api/users/**",
                                "/api/student/**",
                                "/api/instructor/**",
                                "/api/enrollment/**",
                                "/api/course/**",
                                "/api/lesson/**",
                                "/api/quiz/**",
                                "/api/assignment/**",

                                // ✅ Swagger and API documentation endpoints
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()
                        // 🔒 Any other request must be authenticated
                        .anyRequest().authenticated()
                )


                // 🌐 Disable CORS for now (you can enable with proper config later)
                .cors(cors -> cors.disable())

                // 🚫 Disable CSRF for stateless REST APIs
                .csrf(csrf -> csrf.disable())

                // 🚪 Configure logout endpoint
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .clearAuthentication(true)
                        .invalidateHttpSession(true)
                ) // ❌ Disable Spring’s default login mechanisms
                .formLogin(form -> form.disable())     // disable /login HTML form
                .httpBasic(basic -> basic.disable());  // disable HTTP Basic auth



        return http.build();
    }

    /**
     * Provides a BCrypt password encoder for secure password hashing.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ✅ Temporary method — only for generating a hashed password for admin
//    public static void main(String[] args) {
//        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//        String rawPassword = "admin123"; // You can change this
//        System.out.println("Encoded password: " + encoder.encode(rawPassword));
//    }
}




