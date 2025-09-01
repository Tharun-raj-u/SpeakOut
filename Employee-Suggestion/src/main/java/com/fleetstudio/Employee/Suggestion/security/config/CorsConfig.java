package com.fleetstudio.Employee.Suggestion.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Replace the origin with your front-end URL. You can also use "*" if you need to allow all origins.
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173","http://localhost:5174/","http://localhost:5173/*","http://localhost:3000/*","https://speakout-omega.vercel.app/"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Allow the necessary headers including the authorization header
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Apply configuration for all endpoints
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
