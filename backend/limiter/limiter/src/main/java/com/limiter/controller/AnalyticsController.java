package com.limiter.controller;

import com.limiter.dto.AnalyticsDto;
import com.limiter.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/summary")
    public ResponseEntity<?> getAnalyticsSummary() {
        try {
            AnalyticsDto analytics = analyticsService.getAnalyticsSummary();
            
            return ResponseEntity.ok(Map.of(
                "message", "Analytics summary retrieved successfully",
                "analytics", analytics,
                "timestamp", LocalDateTime.now()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/summary/since")
    public ResponseEntity<?> getAnalyticsSummarySince(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime since) {
        try {
            AnalyticsDto analytics = analyticsService.getAnalyticsSummarySince(since);
            
            return ResponseEntity.ok(Map.of(
                "message", "Analytics summary retrieved successfully since " + since,
                "analytics", analytics,
                "since", since,
                "timestamp", LocalDateTime.now()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "status", "healthy",
            "service", "Analytics Service",
            "timestamp", LocalDateTime.now()
        ));
    }
}
