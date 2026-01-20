package com.trading.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Stock Quote from Finnhub API
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockQuote {
    private String symbol;
    private Double currentPrice;
    private Double highPrice;
    private Double lowPrice;
    private Double openPrice;
    private Double previousClose;
    private Long timestamp;
    private Double buyerPrice; // Ask price - price buyers pay
    private Double sellerPrice; // Bid price - price sellers receive
}
