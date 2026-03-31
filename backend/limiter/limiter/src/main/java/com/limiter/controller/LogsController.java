package com.limiter.controller;

import com.limiter.entity.RequestLog;
import com.limiter.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/logs")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LogsController {

    private final LogService logService;

    @GetMapping
    public ResponseEntity<?> getAllLogs() {
        try {
            List<RequestLog> logs = logService.getAllLogs();
            return ResponseEntity.ok(Map.of(
                "message", "Logs retrieved successfully",
                "logs", logs,
                "count", logs.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getLogsByUser(@PathVariable Long userId) {
        try {
            List<RequestLog> logs = logService.getLogsByUser(userId);
            return ResponseEntity.ok(Map.of(
                "message", "User logs retrieved successfully",
                "userId", userId,
                "logs", logs,
                "count", logs.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}/since")
    public ResponseEntity<?> getLogsByUserSince(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime since) {
        try {
            List<RequestLog> logs = logService.getLogsByUserSince(userId, since);
            return ResponseEntity.ok(Map.of(
                "message", "User logs retrieved successfully since " + since,
                "userId", userId,
                "since", since,
                "logs", logs,
                "count", logs.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/endpoint/{endpoint}")
    public ResponseEntity<?> getLogsByEndpoint(@PathVariable String endpoint) {
        try {
            List<RequestLog> logs = logService.getLogsByEndpoint(endpoint);
            return ResponseEntity.ok(Map.of(
                "message", "Endpoint logs retrieved successfully",
                "endpoint", endpoint,
                "logs", logs,
                "count", logs.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getLogsByStatus(@PathVariable Integer status) {
        try {
            List<RequestLog> logs = logService.getLogsByStatus(status);
            return ResponseEntity.ok(Map.of(
                "message", "Status logs retrieved successfully",
                "status", status,
                "logs", logs,
                "count", logs.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/since")
    public ResponseEntity<?> getAllLogsSince(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime since) {
        try {
            List<RequestLog> logs = logService.getAllLogsSince(since);
            return ResponseEntity.ok(Map.of(
                "message", "Logs retrieved successfully since " + since,
                "since", since,
                "logs", logs,
                "count", logs.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
