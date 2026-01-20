# API Testing Guide - Trading Platform

## Quick Start Testing Script

### 1. Register Users (Do this first!)
```bash
# Register Buyer
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "student_buyer",
    "email": "buyer@btech.edu",
    "initialBalance": 100000.00
  }'

# Register Seller
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "student_seller",
    "email": "seller@btech.edu",
    "initialBalance": 100000.00
  }'
```

### 2. Get Stock Quote with Bid/Ask Prices
```bash
# Check AAPL stock
curl -X GET http://localhost:8080/api/stocks/quote/AAPL

# Expected Response:
{
  "symbol": "AAPL",
  "currentPrice": 175.50,
  "highPrice": 177.20,
  "lowPrice": 174.30,
  "openPrice": 176.00,
  "previousClose": 175.00,
  "timestamp": 1735384800,
  "buyerPrice": 175.68,    # Ask price - buyers pay this
  "sellerPrice": 175.32    # Bid price - sellers receive this
}
```

### 3. Test Valid Buy Transaction
```bash
curl -X POST http://localhost:8080/api/trading/buy \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "symbol": "AAPL",
    "quantity": 50,
    "buyerName": "student_buyer"
  }'

# Expected Response:
{
  "success": true,
  "message": "Stock purchased successfully",
  "symbol": "AAPL",
  "quantity": 50,
  "pricePerShare": 175.50,
  "totalAmount": 8775.00,
  "remainingBalance": 91225.00
}
```

### 4. Test Invalid Buy (Non-existent Buyer)
```bash
curl -X POST http://localhost:8080/api/trading/buy \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "symbol": "AAPL",
    "quantity": 10,
    "buyerName": "unknown_user"
  }'

# Expected Response:
{
  "success": false,
  "message": "Buyer 'unknown_user' is not registered in the system. Please register first.",
  "symbol": "AAPL",
  "quantity": 10,
  "pricePerShare": null,
  "totalAmount": null,
  "remainingBalance": 91225.00
}
```

### 5. Buy More Stocks (Diversify Portfolio)
```bash
# Buy GOOGL
curl -X POST http://localhost:8080/api/trading/buy \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "symbol": "GOOGL",
    "quantity": 30,
    "buyerName": "student_buyer"
  }'

# Buy MSFT
curl -X POST http://localhost:8080/api/trading/buy \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "symbol": "MSFT",
    "quantity": 20,
    "buyerName": "student_buyer"
  }'
```

### 6. Check Portfolio with Net Profit
```bash
curl -X GET http://localhost:8080/api/trading/portfolio/1

# Expected Response:
[
  {
    "id": 1,
    "userId": 1,
    "symbol": "AAPL",
    "quantity": 50,
    "avgPurchasePrice": 175.50,
    "currentPrice": 175.50,
    "currentValue": 8775.00,
    "investedAmount": 8775.00,
    "netProfit": 0.00,           # Profit/Loss in dollars
    "profitPercentage": 0.00     # Profit/Loss in percentage
  },
  {
    "id": 2,
    "userId": 1,
    "symbol": "GOOGL",
    "quantity": 30,
    "avgPurchasePrice": 140.30,
    "currentPrice": 140.30,
    "currentValue": 4209.00,
    "investedAmount": 4209.00,
    "netProfit": 0.00,
    "profitPercentage": 0.00
  }
]
```

### 7. Test Valid Sell Transaction
```bash
curl -X POST http://localhost:8080/api/trading/sell \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "symbol": "AAPL",
    "quantity": 20,
    "sellerName": "student_seller"
  }'

# Expected Response:
{
  "success": true,
  "message": "Stock sold successfully",
  "symbol": "AAPL",
  "quantity": 20,
  "pricePerShare": 175.50,
  "totalAmount": 3510.00,
  "remainingBalance": 94735.00
}
```

### 8. Test Invalid Sell (Non-existent Seller)
```bash
curl -X POST http://localhost:8080/api/trading/sell \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "symbol": "AAPL",
    "quantity": 10,
    "sellerName": "fake_seller"
  }'

# Expected Response:
{
  "success": false,
  "message": "Seller 'fake_seller' is not registered in the system. Please register first.",
  "symbol": "AAPL",
  "quantity": 10,
  "pricePerShare": null,
  "totalAmount": null,
  "remainingBalance": 94735.00
}
```

### 9. View Transaction History
```bash
curl -X GET http://localhost:8080/api/trading/transactions/1

# Expected Response:
[
  {
    "id": 1,
    "userId": 1,
    "symbol": "AAPL",
    "type": "BUY",
    "quantity": 50,
    "price": 175.50,
    "totalAmount": 8775.00,
    "buyerName": "student_buyer",
    "sellerName": null,
    "transactionDate": "2025-12-28T10:30:00"
  },
  {
    "id": 2,
    "userId": 1,
    "symbol": "AAPL",
    "type": "SELL",
    "quantity": 20,
    "price": 175.50,
    "totalAmount": 3510.00,
    "buyerName": null,
    "sellerName": "student_seller",
    "transactionDate": "2025-12-28T11:15:00"
  }
]
```

## Validation Test Cases

### Test Case 1: Buy with Empty Buyer Name
```bash
curl -X POST http://localhost:8080/api/trading/buy \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "symbol": "AAPL",
    "quantity": 10,
    "buyerName": ""
  }'
# Should succeed (buyerName is optional if empty)
```

### Test Case 2: Sell Without Sufficient Quantity
```bash
curl -X POST http://localhost:8080/api/trading/sell \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "symbol": "TSLA",
    "quantity": 100,
    "sellerName": "student_seller"
  }'

# Expected Response:
{
  "success": false,
  "message": "Stock not found in portfolio"
}
```

### Test Case 3: Buy Without Sufficient Balance
```bash
curl -X POST http://localhost:8080/api/trading/buy \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "symbol": "AAPL",
    "quantity": 10000,
    "buyerName": "student_buyer"
  }'

# Expected Response:
{
  "success": false,
  "message": "Insufficient balance",
  "remainingBalance": 91225.00
}
```

## Demo Presentation Flow

### Step 1: Introduction (2 minutes)
- Explain the trading platform concept
- Show the Swagger UI documentation
- Highlight new features: bid/ask prices, profit tracking, user validation

### Step 2: User Registration (3 minutes)
- Register 2-3 users with different names
- Show how usernames are unique identifiers
- Explain the importance of user validation

### Step 3: Stock Quotes (2 minutes)
- Query multiple stocks (AAPL, GOOGL, MSFT, TSLA)
- Explain buyer price (ask) vs seller price (bid)
- Show the spread concept

### Step 4: Valid Trading Operations (5 minutes)
- Buy stocks with registered buyer name
- Show portfolio update with quantities
- Buy more stocks to show average price calculation
- Demonstrate portfolio with net profit display

### Step 5: Validation Demonstration (3 minutes)
- Attempt buy with non-existent buyer - FAIL
- Attempt sell with non-existent seller - FAIL
- Register the user and retry - SUCCESS
- Emphasize security and data integrity

### Step 6: Profit/Loss Tracking (5 minutes)
- Check portfolio API response
- Highlight:
  * Current value calculation
  * Invested amount
  * Net profit (positive/negative)
  * Profit percentage
- Explain how this helps traders make decisions

### Step 7: Sell Operations (3 minutes)
- Sell stocks with registered seller name
- Show updated portfolio
- Display transaction history with buyer/seller info

### Step 8: Transaction History (2 minutes)
- Show complete audit trail
- Highlight buyer/seller names in records
- Explain traceability and accountability

## Performance Metrics to Highlight

1. **Data Integrity**: 100% validation of buyer/seller names
2. **Traceability**: Every transaction linked to registered users
3. **Real-time Profit**: Instant calculation of profit/loss
4. **Accuracy**: Precise average price calculations
5. **Security**: No anonymous or invalid trades

## Common Errors and Solutions

| Error | Cause | Solution |
|-------|-------|----------|
| "Buyer not registered" | Using non-existent username | Register user first or use existing username |
| "Seller not registered" | Using non-existent username | Register user first or use existing username |
| "Insufficient balance" | Not enough funds | Add more balance or reduce quantity |
| "Stock not found in portfolio" | Trying to sell unowned stock | Buy the stock first |
| "Insufficient stock quantity" | Trying to sell more than owned | Reduce sell quantity |

## Swagger UI Testing

Access: http://localhost:8080/swagger-ui.html

1. Navigate to "User Controller" - Register users
2. Navigate to "Stock Market" - Get quotes
3. Navigate to "Trading Operations":
   - Test /buy endpoint with valid buyer name
   - Test /buy endpoint with invalid buyer name
   - Test /sell endpoint with valid seller name
   - Test /sell endpoint with invalid seller name
   - Check /portfolio endpoint for net profit
   - Check /transactions endpoint for history

## Advanced Testing Scenarios

### Multi-User Trading Simulation
```bash
# User 1 buys AAPL
curl -X POST http://localhost:8080/api/trading/buy \
  -d '{"userId": 1, "symbol": "AAPL", "quantity": 100, "buyerName": "student_buyer"}'

# User 2 buys AAPL
curl -X POST http://localhost:8080/api/trading/buy \
  -d '{"userId": 2, "symbol": "AAPL", "quantity": 50, "buyerName": "student_seller"}'

# Check both portfolios for profit comparison
curl http://localhost:8080/api/trading/portfolio/1
curl http://localhost:8080/api/trading/portfolio/2
```

### Average Price Calculation Demo
```bash
# Buy 10 shares at current price
curl -X POST http://localhost:8080/api/trading/buy \
  -d '{"userId": 1, "symbol": "TSLA", "quantity": 10, "buyerName": "student_buyer"}'

# Check average price
curl http://localhost:8080/api/trading/portfolio/1

# Buy 10 more shares (price might have changed)
curl -X POST http://localhost:8080/api/trading/buy \
  -d '{"userId": 1, "symbol": "TSLA", "quantity": 10, "buyerName": "student_buyer"}'

# Check new average price
curl http://localhost:8080/api/trading/portfolio/1
```

## Conclusion

This testing guide demonstrates:
- Complete validation of buyer/seller identities
- Real-time profit/loss tracking in portfolio
- Bid/ask price display for informed trading
- End-to-end transaction flow with security
- Professional-grade trading platform features

Perfect for B.Tech project demonstrations and understanding real-world trading systems!
