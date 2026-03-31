package com.limiter.service;

import com.limiter.dto.AnalyticsDto;
import com.limiter.repository.RequestLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {

    private final RequestLogRepository requestLogRepository;

    public AnalyticsDto getAnalyticsSummary() {
        try {
            // Get overall statistics
            Long totalRequests = requestLogRepository.count();
            Long successRequests = requestLogRepository.countSuccessRequests();
            Long failedRequests = requestLogRepository.countFailedRequests();
            
            // Calculate rates
            double successRate = totalRequests > 0 ? (successRequests.doubleValue() / totalRequests) * 100 : 0.0;
            double failureRate = totalRequests > 0 ? (failedRequests.doubleValue() / totalRequests) * 100 : 0.0;
            
            // Get top users
            List<AnalyticsDto.UserRequestCount> topUsers = getTopUsers();
            
            // Get top endpoints
            List<AnalyticsDto.EndpointRequestCount> topEndpoints = getTopEndpoints();
            
            // Get requests by method
            List<AnalyticsDto.MethodRequestCount> requestsByMethod = getRequestsByMethod();
            
            // Additional metrics
            Map<String, Object> additionalMetrics = getAdditionalMetrics();
            
            return AnalyticsDto.builder()
                    .totalRequests(totalRequests)
                    .successRequests(successRequests)
                    .failedRequests(failedRequests)
                    .successRate(Math.round(successRate * 100.0) / 100.0)
                    .failureRate(Math.round(failureRate * 100.0) / 100.0)
                    .topUsers(topUsers)
                    .topEndpoints(topEndpoints)
                    .requestsByMethod(requestsByMethod)
                    .additionalMetrics(additionalMetrics)
                    .build();
                    
        } catch (Exception e) {
            log.error("Error generating analytics summary: {}", e.getMessage(), e);
            return getEmptyAnalytics();
        }
    }
    
    public AnalyticsDto getAnalyticsSummarySince(LocalDateTime since) {
        try {
            // Get statistics since specified time
            Long totalRequests = requestLogRepository.countSince(since);
            
            // For time-based analytics, we'll need to calculate success/failure ratios differently
            // This is a simplified version - in production you might want more sophisticated time-based queries
            List<AnalyticsDto.EndpointRequestCount> topEndpoints = getTopEndpointsSince(since);
            
            return AnalyticsDto.builder()
                    .totalRequests(totalRequests)
                    .successRequests(0L) // Would need additional queries
                    .failedRequests(0L)   // Would need additional queries
                    .successRate(0.0)
                    .failureRate(0.0)
                    .topEndpoints(topEndpoints)
                    .additionalMetrics(Map.of("since", since.toString()))
                    .build();
                    
        } catch (Exception e) {
            log.error("Error generating analytics summary since {}: {}", since, e.getMessage(), e);
            return getEmptyAnalytics();
        }
    }
    
    private List<AnalyticsDto.UserRequestCount> getTopUsers() {
        List<AnalyticsDto.UserRequestCount> userCounts = new ArrayList<>();
        
        try {
            List<Object[]> results = requestLogRepository.countRequestsByUser();
            
            for (Object[] result : results) {
                Long userId = (Long) result[0];
                Long requestCount = (Long) result[1];
                
                // Get success and failure counts for this user
                Long successCount = requestLogRepository.countSuccessRequestsByUser(userId);
                Long failedCount = requestLogRepository.countFailedRequestsByUser(userId);
                
                double userSuccessRate = requestCount > 0 ? (successCount.doubleValue() / requestCount) * 100 : 0.0;
                
                userCounts.add(AnalyticsDto.UserRequestCount.builder()
                        .userId(userId)
                        .requestCount(requestCount)
                        .successCount(successCount)
                        .failedCount(failedCount)
                        .successRate(Math.round(userSuccessRate * 100.0) / 100.0)
                        .build());
            }
            
        } catch (Exception e) {
            log.error("Error getting top users: {}", e.getMessage(), e);
        }
        
        return userCounts;
    }
    
    private List<AnalyticsDto.EndpointRequestCount> getTopEndpoints() {
        List<AnalyticsDto.EndpointRequestCount> endpointCounts = new ArrayList<>();
        
        try {
            List<Object[]> results = requestLogRepository.countRequestsByEndpoint();
            Long totalRequests = requestLogRepository.count();
            
            for (Object[] result : results) {
                String endpoint = (String) result[0];
                Long requestCount = (Long) result[1];
                
                double percentage = totalRequests > 0 ? (requestCount.doubleValue() / totalRequests) * 100 : 0.0;
                
                endpointCounts.add(AnalyticsDto.EndpointRequestCount.builder()
                        .endpoint(endpoint)
                        .requestCount(requestCount)
                        .percentage(Math.round(percentage * 100.0) / 100.0)
                        .build());
            }
            
        } catch (Exception e) {
            log.error("Error getting top endpoints: {}", e.getMessage(), e);
        }
        
        return endpointCounts;
    }
    
    private List<AnalyticsDto.EndpointRequestCount> getTopEndpointsSince(LocalDateTime since) {
        List<AnalyticsDto.EndpointRequestCount> endpointCounts = new ArrayList<>();
        
        try {
            List<Object[]> results = requestLogRepository.countRequestsByEndpointSince(since);
            Long totalRequests = requestLogRepository.countSince(since);
            
            for (Object[] result : results) {
                String endpoint = (String) result[0];
                Long requestCount = (Long) result[1];
                
                double percentage = totalRequests > 0 ? (requestCount.doubleValue() / totalRequests) * 100 : 0.0;
                
                endpointCounts.add(AnalyticsDto.EndpointRequestCount.builder()
                        .endpoint(endpoint)
                        .requestCount(requestCount)
                        .percentage(Math.round(percentage * 100.0) / 100.0)
                        .build());
            }
            
        } catch (Exception e) {
            log.error("Error getting top endpoints since {}: {}", since, e.getMessage(), e);
        }
        
        return endpointCounts;
    }
    
    private List<AnalyticsDto.MethodRequestCount> getRequestsByMethod() {
        List<AnalyticsDto.MethodRequestCount> methodCounts = new ArrayList<>();
        
        try {
            List<Object[]> results = requestLogRepository.countRequestsByMethod();
            Long totalRequests = requestLogRepository.count();
            
            for (Object[] result : results) {
                String method = (String) result[0];
                Long requestCount = (Long) result[1];
                
                double percentage = totalRequests > 0 ? (requestCount.doubleValue() / totalRequests) * 100 : 0.0;
                
                methodCounts.add(AnalyticsDto.MethodRequestCount.builder()
                        .method(method)
                        .requestCount(requestCount)
                        .percentage(Math.round(percentage * 100.0) / 100.0)
                        .build());
            }
            
        } catch (Exception e) {
            log.error("Error getting requests by method: {}", e.getMessage(), e);
        }
        
        return methodCounts;
    }
    
    private Map<String, Object> getAdditionalMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        try {
            // Add timestamp of analysis
            metrics.put("analysisTimestamp", LocalDateTime.now());
            
            // Add unique user count (approximate)
            List<Object[]> userResults = requestLogRepository.countRequestsByUser();
            metrics.put("uniqueUsers", userResults.size());
            
            // Add unique endpoint count
            List<Object[]> endpointResults = requestLogRepository.countRequestsByEndpoint();
            metrics.put("uniqueEndpoints", endpointResults.size());
            
        } catch (Exception e) {
            log.error("Error getting additional metrics: {}", e.getMessage(), e);
        }
        
        return metrics;
    }
    
    private AnalyticsDto getEmptyAnalytics() {
        return AnalyticsDto.builder()
                .totalRequests(0L)
                .successRequests(0L)
                .failedRequests(0L)
                .successRate(0.0)
                .failureRate(0.0)
                .topUsers(new ArrayList<>())
                .topEndpoints(new ArrayList<>())
                .requestsByMethod(new ArrayList<>())
                .additionalMetrics(new HashMap<>())
                .build();
    }
}
