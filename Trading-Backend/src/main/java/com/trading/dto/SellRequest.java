package com.trading.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Stock Sell Request
 * Includes buyer name (person who will receive the stocks) validation as per trading standards
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SellRequest {
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotBlank(message = "Stock symbol is required")
    private String symbol;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
    
    @NotBlank(message = "Buyer name is required for trade verification")
    private String sellerName; // Note: Despite the name, this is actually the BUYER username (person receiving stocks)
}
