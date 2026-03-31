package com.limiter.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsDto {
    
    private Long totalRequests;
    private Long successRequests;
    private Long failedRequests;
    private Double successRate;
    private Double failureRate;
    
    private List<UserRequestCount> topUsers;
    private List<EndpointRequestCount> topEndpoints;
    private List<MethodRequestCount> requestsByMethod;
    
    private Map<String, Object> additionalMetrics;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserRequestCount {
        private Long userId;
        private Long requestCount;
        private Long successCount;
        private Long failedCount;
        private Double successRate;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EndpointRequestCount {
        private String endpoint;
        private Long requestCount;
        private Double percentage;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MethodRequestCount {
        private String method;
        private Long requestCount;
        private Double percentage;
    }
}
