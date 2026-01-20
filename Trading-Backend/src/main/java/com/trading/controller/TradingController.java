package com.trading.controller;

import com.trading.dto.PortfolioResponse;
import com.trading.dto.SellRequest;
import com.trading.dto.TradeRequest;
import com.trading.dto.TradeResponse;
import com.trading.model.Portfolio;
import com.trading.model.Transaction;
import com.trading.service.TradingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for Trading operations
 */
@RestController
@RequestMapping("/api/trading")
@Tag(name = "Trading Operations", description = "APIs for buying and selling stocks")
public class TradingController {

    @Autowired
    private TradingService tradingService;

    /**
     * Buy stocks
     */
    @PostMapping("/buy")
    @Operation(
        summary = "Buy stocks", 
        description = "Purchase stocks. Requires userId, stock symbol, and quantity. Deducts amount from user balance."
    )
    public ResponseEntity<TradeResponse> buyStock(@Valid @RequestBody TradeRequest request) {
        try {
            TradeResponse response = tradingService.buyStock(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new TradeResponse(false, e.getMessage(), null, null, null, null, null));
        }
    }

    /**
     * Sell stocks
     */
    @PostMapping("/sell")
    @Operation(
        summary = "Sell stocks to another user", 
        description = "Peer-to-peer stock transfer. Sells stocks from seller's portfolio and transfers them to buyer's portfolio. Requires userId (seller), stock symbol, quantity, and sellerName (buyer's username who receives the stocks). Buyer must have sufficient balance."
    )
    public ResponseEntity<TradeResponse> sellStock(@Valid @RequestBody SellRequest request) {
        try {
            TradeResponse response = tradingService.sellStock(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new TradeResponse(false, e.getMessage(), null, null, null, null, null));
        }
    }

    /**
     * Get user portfolio
     */
    @GetMapping("/portfolio/{userId}")
    @Operation(
        summary = "Get user portfolio", 
        description = "Retrieve all stock holdings for a specific user with current market value and real-time net profit/loss"
    )
    public ResponseEntity<?> getUserPortfolio(@PathVariable Long userId) {
        try {
            List<PortfolioResponse> portfolio = tradingService.getUserPortfolio(userId);
            return ResponseEntity.ok(portfolio);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "message", "Error retrieving portfolio: " + e.getMessage()
            ));
        }
    }

    /**
     * Get user transaction history
     */
    @GetMapping("/transactions/{userId}")
    @Operation(
        summary = "Get transaction history", 
        description = "Retrieve all buy/sell transactions for a specific user ordered by date (newest first)"
    )
    public ResponseEntity<?> getUserTransactions(@PathVariable Long userId) {
        try {
            List<Transaction> transactions = tradingService.getUserTransactions(userId);
            return ResponseEntity.ok(transactions);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "message", "Error retrieving transactions: " + e.getMessage()
            ));
        }
    }
}
