package com.limiter.service;

import com.limiter.dto.RateLimitRuleDto;
import com.limiter.dto.RateLimitRuleUpdateDto;
import com.limiter.entity.RateLimitRule;
import com.limiter.repository.RateLimitRepository;
import com.limiter.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RateLimitService {

    private final RateLimitRepository rateLimitRepository;
    private final UserRepository userRepository;

    public RateLimitRule createRule(RateLimitRuleDto dto) {
        if (!userRepository.existsById(dto.getUserId())) {
            throw new RuntimeException("User not found with ID: " + dto.getUserId());
        }

        if (rateLimitRepository.existsByUserId(dto.getUserId())) {
            throw new RuntimeException("Rate limit rule already exists for user ID: " + dto.getUserId());
        }

        RateLimitRule rule = RateLimitRule.builder()
                .userId(dto.getUserId())
                .limitCount(dto.getLimitCount())
                .timeWindow(dto.getTimeWindow())
                .build();

        return rateLimitRepository.save(rule);
    }

    public Optional<RateLimitRule> getRuleByUserId(Long userId) {
        return rateLimitRepository.findByUserId(userId);
    }

    public List<RateLimitRule> getAllRules() {
        return rateLimitRepository.findAll();
    }

    public RateLimitRule updateRule(Long id, RateLimitRuleUpdateDto dto) {
        RateLimitRule rule = rateLimitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rate limit rule not found with ID: " + id));

        if (dto.getLimitCount() != null) {
            rule.setLimitCount(dto.getLimitCount());
        }
        if (dto.getTimeWindow() != null) {
            rule.setTimeWindow(dto.getTimeWindow());
        }

        return rateLimitRepository.save(rule);
    }

    public void deleteRule(Long id) {
        if (!rateLimitRepository.existsById(id)) {
            throw new RuntimeException("Rate limit rule not found with ID: " + id);
        }
        rateLimitRepository.deleteById(id);
    }

    public void deleteRuleByUserId(Long userId) {
        if (!rateLimitRepository.existsByUserId(userId)) {
            throw new RuntimeException("Rate limit rule not found for user ID: " + userId);
        }
        rateLimitRepository.deleteByUserId(userId);
    }
}
