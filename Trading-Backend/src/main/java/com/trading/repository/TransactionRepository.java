package com.trading.repository;

import com.trading.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Transaction entity operations
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    // Get all transactions for a user ordered by transaction date (newest first)
    @Query("SELECT t FROM Transaction t WHERE t.userId = ?1 ORDER BY t.transactionDate DESC")
    List<Transaction> findByUserId(Long userId);
    
    // Get transactions for a specific symbol ordered by date
    @Query("SELECT t FROM Transaction t WHERE t.userId = ?1 AND t.symbol = ?2 ORDER BY t.transactionDate DESC")
    List<Transaction> findByUserIdAndSymbol(Long userId, String symbol);
}
