package com.trading.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for Portfolio Response with profit/loss calculations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioResponse {
    
    private Long id;
    private Long userId;
    private String symbol;
    private Integer quantity;
    private BigDecimal avgPurchasePrice;
    private BigDecimal currentPrice;
    private BigDecimal currentValue; // quantity * currentPrice
    private BigDecimal investedAmount; // quantity * avgPurchasePrice
    private BigDecimal netProfit; // currentValue - investedAmount
    private Double profitPercentage; // (netProfit / investedAmount) * 100
    
    /**
     * Calculate derived fields
     */
    public void calculateProfitLoss() {
        if (quantity != null && currentPrice != null && avgPurchasePrice != null) {
            this.currentValue = currentPrice.multiply(BigDecimal.valueOf(quantity));
            this.investedAmount = avgPurchasePrice.multiply(BigDecimal.valueOf(quantity));
            this.netProfit = currentValue.subtract(investedAmount);
            
            if (investedAmount.compareTo(BigDecimal.ZERO) > 0) {
                this.profitPercentage = netProfit
                    .divide(investedAmount, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
            } else {
                this.profitPercentage = 0.0;
            }
        }
    }
}
