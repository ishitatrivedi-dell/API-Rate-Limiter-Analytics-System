package com.limiter.service;

import com.limiter.entity.RequestLog;
import com.limiter.repository.RequestLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LogService {

    private final RequestLogRepository requestLogRepository;

    public RequestLog saveLog(Long userId, String endpoint, String method, Integer status) {
        RequestLog log = RequestLog.builder()
                .userId(userId)
                .endpoint(endpoint)
                .method(method)
                .status(status)
                .timestamp(LocalDateTime.now())
                .build();

        return requestLogRepository.save(log);
    }

    public List<RequestLog> getLogsByUser(Long userId) {
        return requestLogRepository.findByUserIdOrderByTimestampDesc(userId);
    }

    public List<RequestLog> getLogsByUserSince(Long userId, LocalDateTime since) {
        return requestLogRepository.findByUserIdSince(userId, since);
    }

    public List<RequestLog> getAllLogs() {
        return requestLogRepository.findAllOrderedByTimestampDesc();
    }

    public List<RequestLog> getAllLogsSince(LocalDateTime since) {
        return requestLogRepository.findAllSince(since);
    }

    public List<RequestLog> getLogsByEndpoint(String endpoint) {
        return requestLogRepository.findByEndpointOrderByTimestampDesc(endpoint);
    }

    public List<RequestLog> getLogsByStatus(Integer status) {
        return requestLogRepository.findByStatusOrderByTimestampDesc(status);
    }
}
