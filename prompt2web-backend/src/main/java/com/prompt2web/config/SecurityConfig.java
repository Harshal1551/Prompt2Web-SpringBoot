package com.prompt2web.config;

import com.prompt2web.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;


    // ------------------------------------------------------------
    // PASSWORD ENCODER
    // ------------------------------------------------------------

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    // ------------------------------------------------------------
    // CORS
    // ------------------------------------------------------------

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of(
                        "http://localhost:5173",
                        "https://prompt2web-frontend.vercel.app"
                )
        );

        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                List.of("*")
        );

        configuration.setExposedHeaders(
                List.of("Content-Disposition")
        );

        configuration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }


    // ------------------------------------------------------------
    // SECURITY FILTER CHAIN
    // ------------------------------------------------------------

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http

                // ------------------------------------------------
                // CSRF
                // ------------------------------------------------

                .csrf(csrf -> csrf.disable())


                // ------------------------------------------------
                // CORS
                // ------------------------------------------------

                .cors(cors ->
                        cors.configurationSource(
                                corsConfigurationSource()
                        )
                )


                // ------------------------------------------------
                // STATELESS SESSION
                // ------------------------------------------------

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )


                // ------------------------------------------------
                // AUTHORIZATION
                // ------------------------------------------------

                .authorizeHttpRequests(auth -> auth

                        // Browser preflight
                        .requestMatchers(
                                HttpMethod.OPTIONS,
                                "/**"
                        ).permitAll()


                        // Public authentication endpoints
                        .requestMatchers(
                                "/api/auth/register",
                                "/api/auth/login",
                                "/api/ai/test",
                                "/api/ai/website-test"
                        ).permitAll()


                        // Public preview resources
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/projects/*/preview/public/**"
                        ).permitAll()


                        // Everything else requires authentication
                        .anyRequest()
                        .authenticated()
                )


                // ------------------------------------------------
                // FRAME / IFRAME SECURITY
                // ------------------------------------------------

                .headers(headers -> headers

                        // X-Frame-Options DENY/SAMEORIGIN
                        // prevents Vercel frontend from framing
                        // Render backend preview.
                        .frameOptions(frame ->
                                frame.disable()
                        )

                        // Allow only our frontend to embed
                        // preview pages.
                        .contentSecurityPolicy(csp ->
                                csp.policyDirectives(
                                        "frame-ancestors 'self' "
                                                + "https://prompt2web-frontend.vercel.app"
                                                + ";"
                                )
                        )
                )


                // ------------------------------------------------
                // JWT FILTER
                // ------------------------------------------------

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );


        return http.build();
    }
}