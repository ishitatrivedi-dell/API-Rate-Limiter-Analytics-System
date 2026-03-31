package com.limiter.filter;

import com.limiter.entity.RateLimitRule;
import com.limiter.entity.RequestLog;
import com.limiter.entity.User;
import com.limiter.repository.RequestLogRepository;
import com.limiter.repository.UserRepository;
import com.limiter.service.RateLimitService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimiterFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;
    private final RateLimitService rateLimitService;
    private final RequestLogRepository requestLogRepository;

    private static final String API_KEY_HEADER = "X-API-KEY";
    private static final String[] EXCLUDED_PATHS = {
        "/h2-console",
        "/actuator",
        "/error",
        "/favicon.ico"
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String path = request.getRequestURI();
        
        // Skip rate limiting for excluded paths
        if (shouldSkipRateLimiting(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract API key from header
        String apiKey = extractApiKey(request);
        if (apiKey == null) {
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, 
                "Missing API key. Please provide X-API-KEY header.");
            return;
        }

        // Find user by API key
        Optional<User> userOptional = userRepository.findByApiKey(apiKey);
        if (userOptional.isEmpty()) {
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, 
                "Invalid API key.");
            return;
        }

        User user = userOptional.get();
        Long userId = user.getId();

        // Get rate limit rule for user
        Optional<RateLimitRule> ruleOptional = rateLimitService.getRuleByUserId(userId);
        if (ruleOptional.isEmpty()) {
            // If no rate limit rule is found, allow the request but log it
            log.warn("No rate limit rule found for user ID: {}. Allowing request.", userId);
            filterChain.doFilter(request, response);
            logRequest(userId, request, response.getStatus());
            return;
        }

        RateLimitRule rule = ruleOptional.get();
        
        // Check if rate limit is exceeded
        if (isRateLimitExceeded(userId, rule)) {
            sendErrorResponse(response, HttpStatus.TOO_MANY_REQUESTS, 
                String.format("Rate limit exceeded. Maximum %d requests per %d seconds allowed.", 
                    rule.getLimitCount(), rule.getTimeWindow()));
            logRequest(userId, request, HttpStatus.TOO_MANY_REQUESTS.value());
            return;
        }

        // Allow the request to proceed
        filterChain.doFilter(request, response);
        
        // Log the request after it's processed
        logRequest(userId, request, response.getStatus());
    }

    private String extractApiKey(HttpServletRequest request) {
        String apiKey = request.getHeader(API_KEY_HEADER);
        if (apiKey != null && !apiKey.trim().isEmpty()) {
            return apiKey.trim();
        }
        return null;
    }

    private boolean shouldSkipRateLimiting(String path) {
        for (String excludedPath : EXCLUDED_PATHS) {
            if (path.startsWith(excludedPath)) {
                return true;
            }
        }
        return false;
    }

    private boolean isRateLimitExceeded(Long userId, RateLimitRule rule) {
        LocalDateTime timeWindowStart = LocalDateTime.now().minusSeconds(rule.getTimeWindow());
        
        // Count requests in the time window
        List<RequestLog> recentRequests = requestLogRepository.findByUserIdSince(userId, timeWindowStart);
        
        int requestCount = recentRequests.size();
        log.debug("User {} has made {} requests in the last {} seconds. Limit: {}", 
                userId, requestCount, rule.getTimeWindow(), rule.getLimitCount());
        
        return requestCount >= rule.getLimitCount();
    }

    private void logRequest(Long userId, HttpServletRequest request, int status) {
        try {
            String method = request.getMethod();
            String endpoint = request.getRequestURI();
            
            log.info("Logging request: User={}, Method={}, Endpoint={}, Status={}", 
                    userId, method, endpoint, status);
            
            // The actual logging is handled by RequestLoggingFilter
            // This is just for debugging purposes
        } catch (Exception e) {
            log.error("Error logging request: {}", e.getMessage(), e);
        }
    }

    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message) 
            throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        
        String jsonResponse = String.format(
            "{\"timestamp\":\"%s\",\"status\":%d,\"error\":\"%s\",\"message\":\"%s\",\"path\":\"%s\"}",
            LocalDateTime.now(),
            status.value(),
            status.getReasonPhrase(),
            message.replace("\"", "\\\""),
            ""
        );
        
        PrintWriter writer = response.getWriter();
        writer.write(jsonResponse);
        writer.flush();
        
        log.warn("Rate limit response: {} - {}", status.value(), message);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // Don't apply rate limiting to static resources and health checks
        return path.contains("/static/") || 
               path.contains("/css/") || 
               path.contains("/js/") || 
               path.contains("/images/") ||
               path.equals("/health") ||
               path.equals("/ping");
    }
}
