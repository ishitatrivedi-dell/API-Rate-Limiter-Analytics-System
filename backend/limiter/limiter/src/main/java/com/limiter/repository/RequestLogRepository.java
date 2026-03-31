package com.limiter.repository;

import com.limiter.entity.RequestLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RequestLogRepository extends JpaRepository<RequestLog, Long> {
    
    List<RequestLog> findByUserIdOrderByTimestampDesc(Long userId);
    
    List<RequestLog> findByEndpointOrderByTimestampDesc(String endpoint);
    
    List<RequestLog> findByStatusOrderByTimestampDesc(Integer status);
    
    @Query("SELECT r FROM RequestLog r WHERE r.userId = :userId AND r.timestamp >= :since ORDER BY r.timestamp DESC")
    List<RequestLog> findByUserIdSince(@Param("userId") Long userId, @Param("since") LocalDateTime since);
    
    @Query("SELECT r FROM RequestLog r WHERE r.timestamp >= :since ORDER BY r.timestamp DESC")
    List<RequestLog> findAllSince(@Param("since") LocalDateTime since);
    
    @Query("SELECT r FROM RequestLog r ORDER BY r.timestamp DESC")
    List<RequestLog> findAllOrderedByTimestampDesc();
}
