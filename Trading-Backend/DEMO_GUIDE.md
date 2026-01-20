# Trading Platform - Demo Guide for B.Tech Students

## Overview
This enhanced trading platform demonstrates real-world stock trading operations with buyer/seller tracking and profit/loss calculations.

## New Enhancements

### 1. Stock Quote with Buyer/Seller Prices
The `/api/stocks/quote/{symbol}` endpoint now includes:
- **buyerPrice** (Ask Price): The price at which buyers can purchase stocks
- **sellerPrice** (Bid Price): The price at which sellers can sell stocks
- Spread of 0.1% between bid and ask prices

### 2. Portfolio with Net Profit
The `/api/trading/portfolio/{userId}` endpoint now shows:
- Current market value of holdings
- Total invested amount
- Net profit/loss in currency
- Profit/loss percentage

### 3. Trading Validations (Following Trading Standards)
**Buy Stocks** (`/api/trading/buy`):
- No buyer/seller name required
- User is automatically the buyer
- Validates: userId, symbol, quantity, sufficient balance

**Sell Stocks** (`/api/trading/sell`):
- **sellerName is MANDATORY** (must be a registered user)
- Validates: userId, symbol, quantity, stock availability, seller registration
- Ensures proper audit trail of who is selling
- **Validation**: System validates that buyer/seller names exist as registered users before processing transactions

## End-to-End Demo Flow

### Step 1: Start the Application
```bash
mvn clean package
java -jar target/trading-platform-1.0.0.jar
```

### Step 2: Access Swagger UI
Open your browser: http://localhost:8080/swagger-ui.html

### Step 3: Register Users
**POST** `/api/users/register`

Student 1 (Buyer):
```json
{
  "username": "student_buyer",
  "email": "buyer@btech.edu",
  "initialBalance": 100000.00
}
```

Student 2 (Seller):
```json
{
  "username": "student_seller",
  "email": "seller@btech.edu",
  "initialBalance": 100000.00
}
```

### Step 4: Check Stock Quotes with Bid/Ask Prices
**GET** `/api/stocks/quote/AAPL`

Response:
```json
{
  "symbol": "AAPL",
  "currentPrice": 175.50,
  "highPrice": 177.20,
  "lowPrice": 174.30,
  "openPrice": 176.00,
  "previousClose": 175.00,
  "timestamp": 1735384800,
  "buyerPrice": 175.68,
  "sellerPrice": 175.32
}
```

**Note:** 
- Buyers pay the **buyerPrice** (Ask - higher price)
- Sellers receive the **sellerPrice** (Bid - lower price)

### Step 5: Buy Stocks
**POST** `/api/trading/buy`

```json
{
  "userId": 1,
  "symbol": "AAPL",
  "quantity": 50
}
```

**Note:** No buyer or seller name needed - the system automatically records the user as the buyer.

Response (Success):
```json
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

### Step 6: Buy More Stocks for Portfolio Diversification
Buy GOOGL stocks:
```json
{
  "userId": 1,
  "symbol": "GOOGL",
  "quantity": 30
}
```

Buy MSFT stocks:
```json
{
  "userId": 1,
  "symbol": "MSFT",
  "quantity": 20
}
```

### Step 7: Check Portfolio with Net Profit
**GET** `/api/trading/portfolio/1`

Response:
```json
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
    "netProfit": 0.00,
    "profitPercentage": 0.00
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
  },
  {
    "id": 3,
    "userId": 1,
    "symbol": "MSFT",
    "quantity": 20,
    "avgPurchasePrice": 380.75,
    "currentPrice": 380.75,
    "currentValue": 7615.00,
    "investedAmount": 7615.00,
    "netProfit": 0.00,
    "profitPercentage": 0.00
  }
]
```

### Step 8: Simulate Price Changes
To demonstrate profit/loss, the mock prices will vary slightly on each request. Wait a moment and check the portfolio again to see profit/loss changes.

### Step 9: Sell Stocks (sellerName is MANDATORY)
**POST** `/api/trading/sell`

```json
{
  "userId": 1,
  "symbol": "AAPL",
  "quantity": 20,
  "sellerName": "student_buyer"
}
```

**Important Validation Rules:**
- `sellerName` is **REQUIRED** for all sell operations
- `sellerName` must be a **registered username** in the system
- The seller must have sufficient stock quantity in their portfolio

**Example Error Responses:**

1. Missing sellerName:
```json
{
  "success": false,
  "message": "Seller name is required for selling stocks. Please provide a valid seller name."
}
```

2. Unregistered seller:
```json
{
  "success": false,
  "message": "Seller 'unknown_user' is not registered in the system. Please register first or use a registered username."
}
```

3. Insufficient quantity:
```json
{
  "success": false,
  "message": "Insufficient stock quantity. Available: 30"
}
```

Response (Success):
```json
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

### Step 10: View Transaction History
**GET** `/api/trading/transactions/1`

Response shows all transactions:
```json
[
  {
    "id": 1,
    "userId": 1,
    "symbol": "AAPL",
    "type": "BUY",
    "quantity": 50,
    "price": 175.50,
    "totalAmount": 8775.00,
    "buyerName": null,
    "sellerName": null,
    "transactionDate": "2025-12-28T10:30:00"
  },
  {
    "id": 4,
    "userId": 1,
    "symbol": "AAPL",
    "type": "SELL",
    "quantity": 20,
    "price": 175.50,
    "totalAmount": 3510.00,
    "buyerName": null,
    "sellerName": "student_buyer",
    "transactionDate": "2025-12-28T11:15:00"
  }
]
```

**Note:** BUY transactions don't track buyer/seller names, but SELL transactions record the sellerName for audit purposes.

## Key Concepts for Students

### 1. Trading Standards & Validations
**Buy Operations:**
- User is automatically the buyer (no need to specify)
- Validates: User exists, sufficient balance, valid symbol
- No buyer/seller tracking needed - buying from market

**Sell Operations:**
- Seller name is **MANDATORY** for audit trail
- Seller must be a **registered user** in the system
- Validates: User exists, owns the stock, sufficient quantity, seller is registered
- Ensures transparency in who is selling stocks

### 2. Bid-Ask Spread
- **Ask Price (Buyer Price)**: Price sellers are asking for
- **Bid Price (Seller Price)**: Price buyers are willing to pay
- The difference is the **spread** - broker's profit margin

### 3. Profit/Loss Calculation
```
Net Profit = Current Value - Invested Amount
Current Value = Quantity × Current Price
Invested Amount = Quantity × Average Purchase Price
Profit % = (Net Profit / Invested Amount) × 100
```

### 4. Average Purchase Price
When buying the same stock multiple times:
```
New Avg Price = (Existing Value + New Purchase) / Total Quantity
```

## Testing Scenarios

### Scenario 1: Validation Testing - Sell Without sellerName
**Purpose:** Demonstrate mandatory seller validation

1. **Attempt to sell without sellerName:**
```json
POST /api/trading/sell
{
  "userId": 1,
  "symbol": "AAPL",
  "quantity": 10
}
```
**Expected Response:**
```json
{
  "success": false,
  "message": "Seller name is required for selling stocks. Please provide a valid seller name.",
  "remainingBalance": 100000.00
}
```

2. **Attempt to sell with non-existent seller:**
```json
POST /api/trading/sell
{
  "userId": 1,
  "symbol": "AAPL",
  "quantity": 10,
  "sellerName": "fake_seller"
}
```
**Expected Response:**
```json
{
  "success": false,
  "message": "Seller 'fake_seller' is not registered in the system. Please register first or use a registered username.",
  "remainingBalance": 100000.00
}
```

3. **Valid sell transaction with registered username:**
   - Use the registered username (e.g., "student_buyer")
   - Transaction succeeds

### Scenario 2: Profitable Trade
1. Register a user first (e.g., username: "trader_john")
2. Buy 100 shares of TSLA at $245.60 (no buyer name needed)
3. Wait for price to increase (or modify mock data)
4. Check portfolio - see positive net profit
5. Sell 50 shares at higher price with `sellerName: "trader_john"`
6. View transaction history

### Scenario 3: Loss-Making Trade
1. Register a user (e.g., username: "trader_sarah")
2. Buy 50 shares of GOOGL at $140.30
3. Wait for price decrease (or modify mock data)
4. Check portfolio - see negative net profit
5. Decision: Hold or sell at loss? (If selling, must provide sellerName)

### Scenario 4: Portfolio Diversification
1. Buy stocks from multiple companies
2. Some stocks in profit, others in loss
3. Calculate overall portfolio performance
4. Make data-driven decisions

## Database Schema

### Portfolio Table
```sql
- id (Primary Key)
- user_id
- symbol
- quantity
- avg_purchase_price
- created_at
- updated_at
```

### Transaction Table
```sql
- id (Primary Key)
- user_id
- symbol
- type (BUY/SELL)
- quantity
- price
- total_amount
- buyer_name (NEW)
- seller_name (NEW)
- transaction_date
```

## API Endpoints Summary

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/stocks/quote/{symbol} | Get stock quote with bid/ask prices |
| POST | /api/users/register | Register new user |
| POST | /api/trading/buy | Buy stocks with buyer name |
| POST | /api/trading/sell | Sell stocks with seller name |
| GET | /api/trading/portfolio/{userId} | Get portfolio with net profit |
| GET | /api/trading/transactions/{userId} | Get transaction history |

## Learning Objectives

1. **RESTful API Design**: CRUD operations, HTTP methods
2. **Database Relationships**: One-to-many (User-Portfolio, User-Transactions)
3. **Business Logic**: Profit/loss calculations, average pricing
4. **DTO Pattern**: Separation of concerns (Entity vs Response)
5. **Transaction Management**: @Transactional for data consistency
6. **Real-time Data**: Integration with external APIs (Finnhub)
7. **Error Handling**: Validation, exception handling
8. **Documentation**: Swagger/OpenAPI

## Advanced Features to Explore

1. Add real-time WebSocket for live price updates
2. Implement stop-loss and limit orders
3. Add portfolio analytics and charts
4. Create trading strategies (buy signals, sell signals)
5. Add market news integration
6. Implement order book (buyers vs sellers)
7. Add commission/transaction fees
8. Implement tax calculation on profits

## Conclusion

This enhanced trading platform provides hands-on experience with:
- Full-stack development (Backend with Spring Boot)
- Database design and ORM (JPA/Hibernate)
- RESTful API development
- Business logic implementation
- Real-world financial concepts

Perfect for B.Tech final year projects and demonstrations!

---
**Note**: This uses mock data for demonstration. For production, integrate with real stock APIs and implement proper security, authentication, and risk management.
