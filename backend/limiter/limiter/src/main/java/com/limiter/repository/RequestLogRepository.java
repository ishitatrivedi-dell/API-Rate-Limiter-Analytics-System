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
    
    // Analytics queries
    @Query("SELECT COUNT(r) FROM RequestLog r WHERE r.userId = :userId")
    Long countByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(r) FROM RequestLog r WHERE r.endpoint = :endpoint")
    Long countByEndpoint(@Param("endpoint") String endpoint);
    
    @Query("SELECT COUNT(r) FROM RequestLog r WHERE r.status >= 200 AND r.status < 300")
    Long countSuccessRequests();
    
    @Query("SELECT COUNT(r) FROM RequestLog r WHERE r.status >= 400")
    Long countFailedRequests();
    
    @Query("SELECT COUNT(r) FROM RequestLog r WHERE r.userId = :userId AND r.status >= 200 AND r.status < 300")
    Long countSuccessRequestsByUser(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(r) FROM RequestLog r WHERE r.userId = :userId AND r.status >= 400")
    Long countFailedRequestsByUser(@Param("userId") Long userId);
    
    @Query("SELECT r.endpoint, COUNT(r) FROM RequestLog r GROUP BY r.endpoint ORDER BY COUNT(r) DESC")
    List<Object[]> countRequestsByEndpoint();
    
    @Query("SELECT r.userId, COUNT(r) FROM RequestLog r WHERE r.userId IS NOT NULL GROUP BY r.userId ORDER BY COUNT(r) DESC")
    List<Object[]> countRequestsByUser();
    
    @Query("SELECT r.method, COUNT(r) FROM RequestLog r GROUP BY r.method ORDER BY COUNT(r) DESC")
    List<Object[]> countRequestsByMethod();
    
    @Query("SELECT COUNT(r) FROM RequestLog r WHERE r.timestamp >= :since")
    Long countSince(@Param("since") LocalDateTime since);
    
    @Query("SELECT r.endpoint, COUNT(r) FROM RequestLog r WHERE r.timestamp >= :since GROUP BY r.endpoint ORDER BY COUNT(r) DESC")
    List<Object[]> countRequestsByEndpointSince(@Param("since") LocalDateTime since);
}
