package com.limiter.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RateLimitRuleDto {
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotNull(message = "Limit count is required")
    @Min(value = 1, message = "Limit count must be at least 1")
    private Integer limitCount;
    
    @NotNull(message = "Time window is required")
    @Min(value = 1, message = "Time window must be at least 1 second")
    private Integer timeWindow;
}
