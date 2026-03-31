package com.limiter.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class RateLimitRuleUpdateDto {
    
    @Min(value = 1, message = "Limit count must be at least 1")
    private Integer limitCount;
    
    @Min(value = 1, message = "Time window must be at least 1 second")
    private Integer timeWindow;
}
