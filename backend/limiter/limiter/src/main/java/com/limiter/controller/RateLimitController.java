package com.limiter.controller;

import com.limiter.dto.RateLimitRuleDto;
import com.limiter.dto.RateLimitRuleUpdateDto;
import com.limiter.entity.RateLimitRule;
import com.limiter.service.RateLimitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/rate-limit")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RateLimitController {

    private final RateLimitService rateLimitService;

    @PostMapping
    public ResponseEntity<?> createRateLimitRule(@Valid @RequestBody RateLimitRuleDto dto) {
        try {
            RateLimitRule rule = rateLimitService.createRule(dto);
            return ResponseEntity.ok(Map.of(
                "message", "Rate limit rule created successfully",
                "id", rule.getId(),
                "userId", rule.getUserId(),
                "limitCount", rule.getLimitCount(),
                "timeWindow", rule.getTimeWindow(),
                "createdAt", rule.getCreatedAt()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllRateLimitRules() {
        try {
            List<RateLimitRule> rules = rateLimitService.getAllRules();
            return ResponseEntity.ok(Map.of(
                "message", "Rate limit rules retrieved successfully",
                "rules", rules
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getRateLimitRuleByUserId(@PathVariable Long userId) {
        try {
            Optional<RateLimitRule> rule = rateLimitService.getRuleByUserId(userId);
            if (rule.isPresent()) {
                return ResponseEntity.ok(Map.of(
                    "message", "Rate limit rule retrieved successfully",
                    "rule", rule.get()
                ));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRateLimitRule(
            @PathVariable Long id,
            @Valid @RequestBody RateLimitRuleUpdateDto dto) {
        try {
            RateLimitRule rule = rateLimitService.updateRule(id, dto);
            return ResponseEntity.ok(Map.of(
                "message", "Rate limit rule updated successfully",
                "id", rule.getId(),
                "userId", rule.getUserId(),
                "limitCount", rule.getLimitCount(),
                "timeWindow", rule.getTimeWindow(),
                "updatedAt", rule.getUpdatedAt()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRateLimitRule(@PathVariable Long id) {
        try {
            rateLimitService.deleteRule(id);
            return ResponseEntity.ok(Map.of(
                "message", "Rate limit rule deleted successfully"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<?> deleteRateLimitRuleByUserId(@PathVariable Long userId) {
        try {
            rateLimitService.deleteRuleByUserId(userId);
            return ResponseEntity.ok(Map.of(
                "message", "Rate limit rule deleted successfully for user"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
