package com.trading.repository;

import com.trading.model.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Portfolio entity operations
 */
@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    
    // Get all portfolio items for a user ordered by symbol
    @Query("SELECT p FROM Portfolio p WHERE p.userId = ?1 ORDER BY p.symbol ASC")
    List<Portfolio> findByUserId(Long userId);
    
    Optional<Portfolio> findByUserIdAndSymbol(Long userId, String symbol);
    
    boolean existsByUserIdAndSymbol(Long userId, String symbol);
}
