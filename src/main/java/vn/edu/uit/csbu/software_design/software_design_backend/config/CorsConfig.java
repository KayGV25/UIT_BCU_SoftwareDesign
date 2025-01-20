package vn.edu.uit.csbu.software_design.software_design_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * The type Cors config.
 */
@Configuration
/**
 * This class likely contains configuration settings for handling Cross-Origin Resource Sharing (CORS)
 * in a Java application.
 */
public class CorsConfig {
    /**
     * This Java function configures CORS (Cross-Origin Resource Sharing) settings for a web
     * application, allowing requests from any origin with specified methods and headers.
     *
     * @return A WebMvcConfigurer bean is being returned.
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@SuppressWarnings("null") CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*");
                        // .allowCredentials(true);
            }
        };
    }

    /**
     * Instantiates a new Cors config.
     */
    public CorsConfig() { }
}
