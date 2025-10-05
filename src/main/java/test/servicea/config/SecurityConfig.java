package test.servicea.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Security configuration class for the application.
 * This class provides configurations for HTTP security and CORS settings.
 */
@Configuration
public class SecurityConfig {

  /**
   * Defines the security filter chain for the application.
   * Configures HTTP security settings, including disabling CSRF protection, enabling CORS,
   * and specifying authorization rules for request matching.
   *
   * @param http the {@link HttpSecurity} to configure HTTP security for the application
   * @return a {@link SecurityFilterChain} instance constructed with the specified security configurations
   * @throws Exception if an error occurs during the configuration process
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .cors(Customizer.withDefaults())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/cats/**", "/h2-console/**").permitAll()
            .anyRequest().permitAll()
        );

    // For H2 console frames if you use it
    http.headers(headers ->
        headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));

    return http.build();
  }

  /**
   * Configures the Cross-Origin Resource Sharing (CORS) settings for the application.
   * This method defines the allowed origins, allowed methods, allowed headers, exposed headers,
   * credentials policy, and maximum age for preflight requests. The configuration is applied
   * to all endpoints within the application.
   *
   * @return a {@link CorsConfigurationSource} object containing the CORS configuration settings
   */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOriginPatterns(List.of("*"));
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setExposedHeaders(List.of("Authorization", "Link"));
    configuration.setAllowCredentials(true);
    configuration.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
