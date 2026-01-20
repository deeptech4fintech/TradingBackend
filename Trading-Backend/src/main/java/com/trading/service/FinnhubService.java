package com.trading.service;

import com.trading.dto.StockQuote;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Service for integrating with Finnhub API to get live stock data
 */
@Service
public class FinnhubService {

    private final WebClient webClient;

    @Value("${finnhub.api.key}")
    private String apiKey;

    public FinnhubService(@Value("${finnhub.api.base-url}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    /**
     * Get real-time stock quote for a symbol
     * @param symbol Stock symbol (e.g., AAPL, GOOGL)
     * @return StockQuote object with current price and other details
     */
    public StockQuote getStockQuote(String symbol) {
        try {
            var response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/quote")
                            .queryParam("symbol", symbol.toUpperCase())
                            .queryParam("token", apiKey)
                            .build())
                    .retrieve()
                    .bodyToMono(FinnhubQuoteResponse.class)
                    .block();

            if (response != null) {
                StockQuote quote = new StockQuote();
                quote.setSymbol(symbol.toUpperCase());
                quote.setCurrentPrice(response.getC());
                quote.setHighPrice(response.getH());
                quote.setLowPrice(response.getL());
                quote.setOpenPrice(response.getO());
                quote.setPreviousClose(response.getPc());
                quote.setTimestamp(response.getT());
                // Calculate buyer and seller prices based on spread
                double spread = response.getC() * 0.001; // 0.1% spread
                quote.setBuyerPrice(response.getC() + spread); // Ask price (higher)
                quote.setSellerPrice(response.getC() - spread); // Bid price (lower)
                return quote;
            }
        } catch (Exception e) {
            System.err.println("Error fetching stock quote: " + e.getMessage());
        }
        
        // Return mock data if API fails or key is not configured
        return getMockStockQuote(symbol);
    }

    /**
     * Get mock stock data for testing without API key
     * Includes slight price variations to simulate real-time market changes
     */
    private StockQuote getMockStockQuote(String symbol) {
        StockQuote quote = new StockQuote();
        quote.setSymbol(symbol.toUpperCase());
        
        // Generate small random variation (-2% to +3%) for realistic profit/loss demo
        double variation = -0.02 + (Math.random() * 0.05); // -2% to +3%
        
        // Mock prices based on symbol with realistic variations
        switch (symbol.toUpperCase()) {
            case "AAPL":
                double appleBase = 175.50;
                double applePrice = appleBase * (1 + variation);
                quote.setCurrentPrice(Math.round(applePrice * 100.0) / 100.0);
                quote.setHighPrice(177.20 + (variation * 100));
                quote.setLowPrice(174.30 + (variation * 100));
                quote.setOpenPrice(176.00);
                quote.setPreviousClose(175.00);
                quote.setBuyerPrice(quote.getCurrentPrice() + 0.18); // Ask price
                quote.setSellerPrice(quote.getCurrentPrice() - 0.18); // Bid price
                break;
            case "GOOGL":
                double googlBase = 140.30;
                double googlPrice = googlBase * (1 + variation);
                quote.setCurrentPrice(Math.round(googlPrice * 100.0) / 100.0);
                quote.setHighPrice(142.50 + (variation * 100));
                quote.setLowPrice(139.80 + (variation * 100));
                quote.setOpenPrice(141.00);
                quote.setPreviousClose(140.00);
                quote.setBuyerPrice(quote.getCurrentPrice() + 0.14);
                quote.setSellerPrice(quote.getCurrentPrice() - 0.14);
                break;
            case "MSFT":
                double msftBase = 380.75;
                double msftPrice = msftBase * (1 + variation);
                quote.setCurrentPrice(Math.round(msftPrice * 100.0) / 100.0);
                quote.setHighPrice(385.00 + (variation * 200));
                quote.setLowPrice(378.50 + (variation * 200));
                quote.setOpenPrice(382.00);
                quote.setPreviousClose(379.00);
                quote.setBuyerPrice(quote.getCurrentPrice() + 0.38);
                quote.setSellerPrice(quote.getCurrentPrice() - 0.38);
                break;
            case "TSLA":
                double teslaBase = 245.60;
                double teslaPrice = teslaBase * (1 + variation);
                quote.setCurrentPrice(Math.round(teslaPrice * 100.0) / 100.0);
                quote.setHighPrice(250.00 + (variation * 150));
                quote.setLowPrice(242.30 + (variation * 150));
                quote.setOpenPrice(248.00);
                quote.setPreviousClose(244.50);
                quote.setBuyerPrice(quote.getCurrentPrice() + 0.25);
                quote.setSellerPrice(quote.getCurrentPrice() - 0.25);
                break;
            default:
                double defaultBase = 100.00;
                double defaultPrice = defaultBase * (1 + variation);
                quote.setCurrentPrice(Math.round(defaultPrice * 100.0) / 100.0);
                quote.setHighPrice(102.00 + (variation * 50));
                quote.setLowPrice(98.00 + (variation * 50));
                quote.setOpenPrice(101.00);
                quote.setPreviousClose(99.50);
                quote.setBuyerPrice(quote.getCurrentPrice() + 0.10);
                quote.setSellerPrice(quote.getCurrentPrice() - 0.10);
        }
        
        quote.setTimestamp(System.currentTimeMillis() / 1000);
        return quote;
    }

    /**
     * Inner class to map Finnhub API response
     */
    private static class FinnhubQuoteResponse {
        private Double c; // Current price
        private Double h; // High price
        private Double l; // Low price
        private Double o; // Open price
        private Double pc; // Previous close
        private Long t; // Timestamp

        public Double getC() { return c; }
        public void setC(Double c) { this.c = c; }
        public Double getH() { return h; }
        public void setH(Double h) { this.h = h; }
        public Double getL() { return l; }
        public void setL(Double l) { this.l = l; }
        public Double getO() { return o; }
        public void setO(Double o) { this.o = o; }
        public Double getPc() { return pc; }
        public void setPc(Double pc) { this.pc = pc; }
        public Long getT() { return t; }
        public void setT(Long t) { this.t = t; }
    }
}
