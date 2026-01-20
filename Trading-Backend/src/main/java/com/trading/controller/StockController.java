package com.trading.controller;

import com.trading.dto.StockQuote;
import com.trading.service.FinnhubService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Stock Market Data
 */
@RestController
@RequestMapping("/api/stocks")
@Tag(name = "Stock Market", description = "APIs for retrieving stock market data")
public class StockController {

    @Autowired
    private FinnhubService finnhubService;

    /**
     * Get stock quote by symbol
     */
    @GetMapping("/quote/{symbol}")
    @Operation(
        summary = "Get stock quote", 
        description = "Get real-time stock quote for a given symbol (e.g., AAPL, GOOGL, MSFT, TSLA)"
    )
    public ResponseEntity<StockQuote> getStockQuote(@PathVariable String symbol) {
        try {
            StockQuote quote = finnhubService.getStockQuote(symbol);
            return ResponseEntity.ok(quote);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
