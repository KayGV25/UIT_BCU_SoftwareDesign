package vn.edu.uit.csbu.software_design.software_design_backend.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import vn.edu.uit.csbu.software_design.software_design_backend.jwt.JWTAuthFilter;

/**
 * The `FilterConfig` class in a Spring Boot application defines a filter registration bean for a JWT
 * authentication filter.
 */
@Configuration
public class FilterConfig {
    /**
     * Logging filter filter registration bean.
     *
     * @return the filter registration bean
     */
// This code snippet is a configuration class in a Spring Boot application that defines a filter
    // registration bean for a JWT authentication filter. Here's a breakdown of what the
    // `loggingFilter()` method is doing:
    @Bean
    public FilterRegistrationBean<JWTAuthFilter> loggingFilter() {
        FilterRegistrationBean<JWTAuthFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new JWTAuthFilter());
        registrationBean.addUrlPatterns("/auth/*"); // Apply filter only to specific paths
        registrationBean.setOrder(1); // Set filter order
        return registrationBean;
    }

    /**
     * Instantiates a new Filter config.
     */
    public FilterConfig() {
    }
}
