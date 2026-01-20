package com.trading.service;

import com.trading.dto.PortfolioResponse;
import com.trading.dto.SellRequest;
import com.trading.dto.TradeRequest;
import com.trading.dto.TradeResponse;
import com.trading.model.Portfolio;
import com.trading.model.Transaction;
import com.trading.model.User;
import com.trading.repository.PortfolioRepository;
import com.trading.repository.TransactionRepository;
import com.trading.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for Trading operations (Buy/Sell stocks)
 */
@Service
public class TradingService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private FinnhubService finnhubService;

    /**
     * Buy stocks
     */
    @Transactional
    public TradeResponse buyStock(TradeRequest request) {
        // Get user
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get current stock price
        var stockQuote = finnhubService.getStockQuote(request.getSymbol());
        BigDecimal currentPrice = BigDecimal.valueOf(stockQuote.getCurrentPrice());
        BigDecimal totalCost = currentPrice.multiply(BigDecimal.valueOf(request.getQuantity()));

        // Check if user has sufficient balance
        if (user.getBalance().compareTo(totalCost) < 0) {
            return new TradeResponse(
                    false, 
                    "Insufficient balance", 
                    request.getSymbol(), 
                    request.getQuantity(), 
                    currentPrice, 
                    totalCost, 
                    user.getBalance()
            );
        }

        // Deduct amount from user balance
        user.setBalance(user.getBalance().subtract(totalCost));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        // Update or create portfolio entry
        Optional<Portfolio> existingPortfolio = portfolioRepository
                .findByUserIdAndSymbol(request.getUserId(), request.getSymbol().toUpperCase());

        Portfolio savedPortfolio;
        if (existingPortfolio.isPresent()) {
            Portfolio portfolio = existingPortfolio.get();
            
            // Calculate new average purchase price
            BigDecimal totalValue = portfolio.getAvgPurchasePrice()
                    .multiply(BigDecimal.valueOf(portfolio.getQuantity()))
                    .add(totalCost);
            int totalQuantity = portfolio.getQuantity() + request.getQuantity();
            BigDecimal newAvgPrice = totalValue.divide(BigDecimal.valueOf(totalQuantity), 2, BigDecimal.ROUND_HALF_UP);
            
            portfolio.setQuantity(totalQuantity);
            portfolio.setAvgPurchasePrice(newAvgPrice);
            portfolio.setUpdatedAt(LocalDateTime.now());
            savedPortfolio = portfolioRepository.save(portfolio);
            System.out.println("Updated portfolio: userId=" + savedPortfolio.getUserId() + ", symbol=" + savedPortfolio.getSymbol() + ", quantity=" + savedPortfolio.getQuantity());
        } else {
            Portfolio portfolio = new Portfolio();
            portfolio.setUserId(request.getUserId());
            portfolio.setSymbol(request.getSymbol().toUpperCase());
            portfolio.setQuantity(request.getQuantity());
            portfolio.setAvgPurchasePrice(currentPrice);
            portfolio.setCreatedAt(LocalDateTime.now());
            portfolio.setUpdatedAt(LocalDateTime.now());
            savedPortfolio = portfolioRepository.save(portfolio);
            System.out.println("Created new portfolio: userId=" + savedPortfolio.getUserId() + ", symbol=" + savedPortfolio.getSymbol() + ", quantity=" + savedPortfolio.getQuantity());
        }

        // Record transaction
        Transaction transaction = new Transaction();
        transaction.setUserId(request.getUserId());
        transaction.setSymbol(request.getSymbol().toUpperCase());
        transaction.setType(Transaction.TransactionType.BUY);
        transaction.setQuantity(request.getQuantity());
        transaction.setPrice(currentPrice);
        transaction.setTotalAmount(totalCost);
        transaction.setTransactionDate(LocalDateTime.now());
        transactionRepository.save(transaction);

        return new TradeResponse(
                true, 
                "Stock purchased successfully", 
                request.getSymbol().toUpperCase(), 
                request.getQuantity(), 
                currentPrice, 
                totalCost, 
                user.getBalance()
        );
    }

    /**
     * Sell stocks with seller validation - transfers stocks to buyer
     * Real-time peer-to-peer trading implementation
     */
    @Transactional
    public TradeResponse sellStock(SellRequest request) {
        // Get seller (the user selling the stock)
        User seller = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Seller user not found"));

        // Validate buyer name exists in system (the person buying from seller)
        User buyer = userRepository.findByUsername(request.getSellerName())
                .orElseThrow(() -> new RuntimeException("Buyer '" + request.getSellerName() + "' is not registered in the system. All buyers must be verified users."));

        // Validation: Prevent self-trading (cannot sell to yourself)
        if (seller.getId().equals(buyer.getId())) {
            return new TradeResponse(
                    false,
                    "Invalid transaction: You cannot sell stocks to yourself. Please specify a different buyer.",
                    request.getSymbol(),
                    request.getQuantity(),
                    null,
                    null,
                    seller.getBalance()
            );
        }

        // Validation: Check username matches
        if (seller.getUsername().equalsIgnoreCase(request.getSellerName())) {
            return new TradeResponse(
                    false,
                    "Invalid transaction: Seller and buyer cannot be the same person (" + request.getSellerName() + ").",
                    request.getSymbol(),
                    request.getQuantity(),
                    null,
                    null,
                    seller.getBalance()
            );
        }

        // Check if seller has the stock in portfolio
        Portfolio sellerPortfolio = portfolioRepository
                .findByUserIdAndSymbol(request.getUserId(), request.getSymbol().toUpperCase())
                .orElseThrow(() -> new RuntimeException("Stock not found in seller's portfolio"));

        // Check if seller has sufficient quantity
        if (sellerPortfolio.getQuantity() < request.getQuantity()) {
            return new TradeResponse(
                    false, 
                    "Insufficient stock quantity. Available: " + sellerPortfolio.getQuantity(), 
                    request.getSymbol(), 
                    request.getQuantity(), 
                    null, 
                    null, 
                    seller.getBalance()
            );
        }

        // Get current stock price
        var stockQuote = finnhubService.getStockQuote(request.getSymbol());
        BigDecimal currentPrice = BigDecimal.valueOf(stockQuote.getCurrentPrice());
        BigDecimal totalAmount = currentPrice.multiply(BigDecimal.valueOf(request.getQuantity()));

        // Check if buyer has sufficient balance
        if (buyer.getBalance().compareTo(totalAmount) < 0) {
            return new TradeResponse(
                    false, 
                    "Buyer '" + buyer.getUsername() + "' has insufficient balance. Required: " + totalAmount + ", Available: " + buyer.getBalance(), 
                    request.getSymbol(), 
                    request.getQuantity(), 
                    currentPrice, 
                    totalAmount, 
                    seller.getBalance()
            );
        }

        // Transfer money: Buyer pays Seller
        buyer.setBalance(buyer.getBalance().subtract(totalAmount));
        buyer.setUpdatedAt(LocalDateTime.now());
        userRepository.save(buyer);

        seller.setBalance(seller.getBalance().add(totalAmount));
        seller.setUpdatedAt(LocalDateTime.now());
        userRepository.save(seller);

        System.out.println("Money transfer: " + buyer.getUsername() + " paid " + totalAmount + " to seller userId=" + seller.getId());

        // Update seller's portfolio (remove stocks)
        int newSellerQuantity = sellerPortfolio.getQuantity() - request.getQuantity();
        System.out.println("Seller portfolio: userId=" + request.getUserId() + ", symbol=" + request.getSymbol() + ", selling=" + request.getQuantity() + ", remaining=" + newSellerQuantity);
        
        if (newSellerQuantity == 0) {
            portfolioRepository.delete(sellerPortfolio);
            System.out.println("Seller portfolio deleted as quantity reached 0");
        } else {
            sellerPortfolio.setQuantity(newSellerQuantity);
            sellerPortfolio.setUpdatedAt(LocalDateTime.now());
            portfolioRepository.save(sellerPortfolio);
            System.out.println("Seller portfolio updated with new quantity: " + newSellerQuantity);
        }

        // Transfer stocks to buyer's portfolio (add stocks)
        Optional<Portfolio> existingBuyerPortfolio = portfolioRepository
                .findByUserIdAndSymbol(buyer.getId(), request.getSymbol().toUpperCase());

        if (existingBuyerPortfolio.isPresent()) {
            Portfolio buyerPortfolio = existingBuyerPortfolio.get();
            
            // Calculate new average purchase price for buyer
            BigDecimal totalValue = buyerPortfolio.getAvgPurchasePrice()
                    .multiply(BigDecimal.valueOf(buyerPortfolio.getQuantity()))
                    .add(totalAmount);
            int totalQuantity = buyerPortfolio.getQuantity() + request.getQuantity();
            BigDecimal newAvgPrice = totalValue.divide(BigDecimal.valueOf(totalQuantity), 2, BigDecimal.ROUND_HALF_UP);
            
            buyerPortfolio.setQuantity(totalQuantity);
            buyerPortfolio.setAvgPurchasePrice(newAvgPrice);
            buyerPortfolio.setUpdatedAt(LocalDateTime.now());
            portfolioRepository.save(buyerPortfolio);
            System.out.println("Buyer portfolio updated: userId=" + buyer.getId() + ", symbol=" + request.getSymbol() + ", newQuantity=" + totalQuantity);
        } else {
            Portfolio buyerPortfolio = new Portfolio();
            buyerPortfolio.setUserId(buyer.getId());
            buyerPortfolio.setSymbol(request.getSymbol().toUpperCase());
            buyerPortfolio.setQuantity(request.getQuantity());
            buyerPortfolio.setAvgPurchasePrice(currentPrice);
            buyerPortfolio.setCreatedAt(LocalDateTime.now());
            buyerPortfolio.setUpdatedAt(LocalDateTime.now());
            portfolioRepository.save(buyerPortfolio);
            System.out.println("Buyer portfolio created: userId=" + buyer.getId() + ", symbol=" + request.getSymbol() + ", quantity=" + request.getQuantity());
        }

        // Record transaction for seller
        Transaction sellerTransaction = new Transaction();
        sellerTransaction.setUserId(request.getUserId());
        sellerTransaction.setSymbol(request.getSymbol().toUpperCase());
        sellerTransaction.setType(Transaction.TransactionType.SELL);
        sellerTransaction.setQuantity(request.getQuantity());
        sellerTransaction.setPrice(currentPrice);
        sellerTransaction.setTotalAmount(totalAmount);
        sellerTransaction.setSellerName(request.getSellerName() + " (sold to)");
        sellerTransaction.setTransactionDate(LocalDateTime.now());
        transactionRepository.save(sellerTransaction);

        // Record transaction for buyer
        Transaction buyerTransaction = new Transaction();
        buyerTransaction.setUserId(buyer.getId());
        buyerTransaction.setSymbol(request.getSymbol().toUpperCase());
        buyerTransaction.setType(Transaction.TransactionType.BUY);
        buyerTransaction.setQuantity(request.getQuantity());
        buyerTransaction.setPrice(currentPrice);
        buyerTransaction.setTotalAmount(totalAmount);
        buyerTransaction.setSellerName("(bought from userId " + seller.getId() + ")");
        buyerTransaction.setTransactionDate(LocalDateTime.now());
        transactionRepository.save(buyerTransaction);

        return new TradeResponse(
                true, 
                "Stock sold successfully to " + request.getSellerName() + ". Stocks transferred to buyer's portfolio.", 
                request.getSymbol().toUpperCase(), 
                request.getQuantity(), 
                currentPrice, 
                totalAmount, 
                seller.getBalance()
        );
    }

    /**
     * Get user's portfolio with profit/loss calculations
     * Real-time trading implementation with current market prices
     */
    public List<PortfolioResponse> getUserPortfolio(Long userId) {
        // Validate user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with ID " + userId + " not found"));
        
        System.out.println("Fetching portfolio for userId: " + userId);
        List<Portfolio> portfolios = portfolioRepository.findByUserId(userId);
        System.out.println("Found " + portfolios.size() + " portfolio entries");
        
        if (portfolios.isEmpty()) {
            System.out.println("No portfolio entries found for userId: " + userId);
            return List.of(); // Return empty list instead of null
        }
        
        return portfolios.stream().map(portfolio -> {
            try {
                PortfolioResponse response = new PortfolioResponse();
                response.setId(portfolio.getId());
                response.setUserId(portfolio.getUserId());
                response.setSymbol(portfolio.getSymbol());
                response.setQuantity(portfolio.getQuantity());
                response.setAvgPurchasePrice(portfolio.getAvgPurchasePrice());
                
                // Get current market price
                var stockQuote = finnhubService.getStockQuote(portfolio.getSymbol());
                response.setCurrentPrice(BigDecimal.valueOf(stockQuote.getCurrentPrice()));
                
                // Calculate real-time profit/loss
                response.calculateProfitLoss();
                
                return response;
            } catch (Exception e) {
                // If unable to fetch current price, use purchase price as fallback
                PortfolioResponse response = new PortfolioResponse();
                response.setId(portfolio.getId());
                response.setUserId(portfolio.getUserId());
                response.setSymbol(portfolio.getSymbol());
                response.setQuantity(portfolio.getQuantity());
                response.setAvgPurchasePrice(portfolio.getAvgPurchasePrice());
                response.setCurrentPrice(portfolio.getAvgPurchasePrice());
                response.calculateProfitLoss();
                return response;
            }
        }).toList();
    }

    /**
     * Get user's transaction history ordered by date (newest first)
     * Real-time trading implementation showing complete transaction history
     */
    public List<Transaction> getUserTransactions(Long userId) {
        // Validate user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with ID " + userId + " not found"));
        
        // Return transactions ordered by date (newest first)
        List<Transaction> transactions = transactionRepository.findByUserId(userId);
        
        return transactions; // Already ordered by query in repository
    }
}
