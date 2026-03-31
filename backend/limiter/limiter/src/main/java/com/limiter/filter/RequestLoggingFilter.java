package com.limiter.filter;

import com.limiter.service.LogService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@Slf4j
public class RequestLoggingFilter implements Filter {

    private final LogService logService;
    private static final Pattern API_KEY_PATTERN = Pattern.compile("Bearer\\s*(.+)");
    private static final Pattern USER_ID_PATTERN = Pattern.compile("/user/(\\d+)");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper((HttpServletResponse) response);

        long startTime = System.currentTimeMillis();
        
        try {
            chain.doFilter(request, responseWrapper);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            
            HttpServletResponse httpResponse = responseWrapper;
            
            String method = httpRequest.getMethod();
            String endpoint = httpRequest.getRequestURI();
            int status = httpResponse.getStatus();
            
            Long userId = extractUserId(httpRequest, endpoint);
            
            logService.saveLog(userId, endpoint, method, status);
            
            log.info("Request: {} {} - Status: {} - Duration: {}ms - User: {}", 
                    method, endpoint, status, duration, userId != null ? userId : "anonymous");
        }
        
        responseWrapper.copyBodyToResponse();
    }

    private Long extractUserId(HttpServletRequest request, String endpoint) {
        // Try to extract user ID from endpoint path
        Matcher userIdMatcher = USER_ID_PATTERN.matcher(endpoint);
        if (userIdMatcher.find()) {
            try {
                return Long.parseLong(userIdMatcher.group(1));
            } catch (NumberFormatException e) {
                log.warn("Invalid user ID in endpoint: {}", endpoint);
            }
        }
        
        // Try to extract user ID from API key (if we had a way to validate it)
        String authorization = request.getHeader("Authorization");
        if (authorization != null) {
            Matcher apiKeyMatcher = API_KEY_PATTERN.matcher(authorization);
            if (apiKeyMatcher.find()) {
                String apiKey = apiKeyMatcher.group(1);
                // Here you would normally validate the API key and get the user ID
                // For now, we'll return null as we don't have API key validation implemented yet
                log.debug("API key found in request: {}", apiKey.substring(0, Math.min(10, apiKey.length())) + "...");
            }
        }
        
        return null;
    }
}
