package vn.edu.uit.csbu.software_design.software_design_backend.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import vn.edu.uit.csbu.software_design.software_design_backend.jwt.JWTAuthFilter;

@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<JWTAuthFilter> loggingFilter() {
        FilterRegistrationBean<JWTAuthFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new JWTAuthFilter());
        registrationBean.addUrlPatterns("/auth/*"); // Apply filter only to specific paths
        registrationBean.setOrder(1); // Set filter order
        return registrationBean;
    }
}
