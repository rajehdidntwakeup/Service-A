package test.servicea;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class ServiceAApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceAApplication.class, args);
    }

    /**
     * Configures CORS (Cross-Origin Resource Sharing) settings for the application.
     * This method defines the allowed origins, methods, headers, and other CORS
     * preferences to enable secure communication from specified sources.
     *
     * @return a WebMvcConfigurer object that configures the CORS mappings for the application
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:8081")
                        .allowedMethods("GET", "POST", "PUT")
                        .allowedHeaders("*")
                        .exposedHeaders("Authorization", "Link")
                        .allowCredentials(true)
                        .maxAge(3600);
            }
        };
    }

}
