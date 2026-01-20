package com.trading.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for Trade Response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TradeResponse {
    private boolean success;
    private String message;
    private String symbol;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal totalAmount;
    private BigDecimal remainingBalance;
}
