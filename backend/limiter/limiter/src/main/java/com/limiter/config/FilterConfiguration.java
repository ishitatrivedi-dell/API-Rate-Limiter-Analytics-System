package com.limiter.config;

import com.limiter.filter.RateLimiterFilter;
import com.limiter.filter.RequestLoggingFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FilterConfiguration {

    private final RateLimiterFilter rateLimiterFilter;
    private final RequestLoggingFilter requestLoggingFilter;

    @Bean
    public FilterRegistrationBean<RateLimiterFilter> rateLimiterFilterRegistration() {
        FilterRegistrationBean<RateLimiterFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(rateLimiterFilter);
        registration.addUrlPatterns("/*");
        registration.setName("rateLimiterFilter");
        registration.setOrder(1); // High priority - run first
        return registration;
    }

    @Bean
    public FilterRegistrationBean<RequestLoggingFilter> requestLoggingFilterRegistration() {
        FilterRegistrationBean<RequestLoggingFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(requestLoggingFilter);
        registration.addUrlPatterns("/*");
        registration.setName("requestLoggingFilter");
        registration.setOrder(2); // Run after rate limiter
        return registration;
    }
}
