# Trading Platform - Spring Boot Application

A mini Spring Boot trading platform demonstrating core trading operations for B.Tech students. This project includes user management, stock trading (buy/sell), portfolio tracking, transaction history, and live stock data integration with Finnhub API.

## 🚀 Features

### Core Functionalities
- **User Management**: Register users with initial balance of $100,000
- **Stock Trading**: Buy and sell stocks with real-time price validation
- **Portfolio Management**: Track stock holdings and average purchase prices
- **Transaction History**: View complete buy/sell transaction logs
- **Live Stock Data**: Integration with Finnhub API for real-time stock quotes
- **Swagger UI**: Interactive API documentation and testing interface
- **H2 Database**: In-memory database for quick testing and development

### Technical Stack
- **Framework**: Spring Boot 3.2.1
- **Language**: Java 17
- **Build Tool**: Maven
- **Database**: H2 (In-Memory)
- **API Documentation**: Swagger/OpenAPI 3.0
- **External API**: Finnhub Stock Market API

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- (Optional) Finnhub API Key from [https://finnhub.io](https://finnhub.io)

## 🛠️ Setup Instructions

### 1. Clone the Repository
```bash
git clone <repository-url>
cd Trading-Backend
```

### 2. Configure Finnhub API (Optional)
Edit `src/main/resources/application.properties`:
```properties
finnhub.api.key=your_actual_api_key_here
```
**Note**: The application includes mock data, so it works without an API key for testing purposes.

### 3. Build the Project
```bash
mvn clean install
```

### 4. Run the Application
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## 📚 API Documentation

### Access Swagger UI
Once the application is running, access the interactive API documentation:
```
http://localhost:8080/swagger-ui.html
```

### H2 Database Console
Access the in-memory database console:
```
URL: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:tradingdb
Username: sa
Password: (leave empty)
```

## 🔌 API Endpoints

### User Management APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/users/register` | Register a new user |
| GET | `/api/users` | Get all users |
| GET | `/api/users/{id}` | Get user by ID |
| GET | `/api/users/username/{username}` | Get user by username |
| DELETE | `/api/users/{id}` | Delete user |

### Stock Market APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/stocks/quote/{symbol}` | Get real-time stock quote |

### Trading Operations APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/trading/buy` | Buy stocks |
| POST | `/api/trading/sell` | Sell stocks |
| GET | `/api/trading/portfolio/{userId}` | Get user portfolio |
| GET | `/api/trading/transactions/{userId}` | Get transaction history |

## 💡 Usage Examples

### 1. Register a New User
```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "password123"
  }'
```

### 2. Get Stock Quote
```bash
curl -X GET http://localhost:8080/api/stocks/quote/AAPL
```

### 3. Buy Stock
```bash
curl -X POST http://localhost:8080/api/trading/buy \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "symbol": "AAPL",
    "quantity": 10
  }'
```

### 4. View Portfolio
```bash
curl -X GET http://localhost:8080/api/trading/portfolio/1
```

### 5. Sell Stock
```bash
curl -X POST http://localhost:8080/api/trading/sell \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "symbol": "AAPL",
    "quantity": 5
  }'
```

### 6. View Transaction History
```bash
curl -X GET http://localhost:8080/api/trading/transactions/1
```

## 🎓 Learning Outcomes for B.Tech Students

This project demonstrates:

1. **RESTful API Design**: Learn how to design and implement REST APIs
2. **Spring Boot Framework**: Understand Spring Boot project structure and annotations
3. **Database Operations**: JPA/Hibernate for database interactions
4. **Service Layer Pattern**: Separation of concerns with service layer
5. **DTO Pattern**: Data Transfer Objects for API requests/responses
6. **Exception Handling**: Global exception handling mechanism
7. **API Documentation**: Swagger/OpenAPI for API documentation
8. **External API Integration**: Consuming third-party APIs (Finnhub)
9. **Transaction Management**: Database transactions for stock trading
10. **Validation**: Input validation using Bean Validation

## 📁 Project Structure

```
src/main/java/com/trading/
├── TradingPlatformApplication.java   # Main application class
├── config/
│   ├── SwaggerConfig.java            # Swagger configuration
│   └── CorsConfig.java               # CORS configuration
├── controller/
│   ├── UserController.java           # User management endpoints
│   ├── StockController.java          # Stock market data endpoints
│   └── TradingController.java        # Trading operations endpoints
├── dto/
│   ├── StockQuote.java               # Stock quote DTO
│   ├── TradeRequest.java             # Trade request DTO
│   ├── TradeResponse.java            # Trade response DTO
│   └── UserRegistrationRequest.java  # User registration DTO
├── model/
│   ├── User.java                     # User entity
│   ├── Portfolio.java                # Portfolio entity
│   └── Transaction.java              # Transaction entity
├── repository/
│   ├── UserRepository.java           # User repository
│   ├── PortfolioRepository.java      # Portfolio repository
│   └── TransactionRepository.java    # Transaction repository
├── service/
│   ├── UserService.java              # User service
│   ├── TradingService.java           # Trading service
│   └── FinnhubService.java           # Finnhub API service
└── exception/
    └── GlobalExceptionHandler.java   # Global exception handler
```

## 🧪 Testing the Application

### Using Swagger UI (Recommended)
1. Navigate to `http://localhost:8080/swagger-ui.html`
2. Try the APIs in the following order:
   - Register a user (POST `/api/users/register`)
   - Get stock quote (GET `/api/stocks/quote/AAPL`)
   - Buy stock (POST `/api/trading/buy`)
   - View portfolio (GET `/api/trading/portfolio/{userId}`)
   - Sell stock (POST `/api/trading/sell`)
   - View transactions (GET `/api/trading/transactions/{userId}`)

### Sample Stock Symbols
- AAPL (Apple)
- GOOGL (Google)
- MSFT (Microsoft)
- TSLA (Tesla)

## 🔧 Configuration

### Application Properties
Located at `src/main/resources/application.properties`:

```properties
# Server Configuration
server.port=8080

# Database Configuration
spring.datasource.url=jdbc:h2:mem:tradingdb
spring.h2.console.enabled=true

# Finnhub API
finnhub.api.key=your_api_key_here

# Swagger
springdoc.swagger-ui.path=/swagger-ui.html
```

## 🐛 Troubleshooting

### Issue: Application fails to start
**Solution**: Ensure Java 17 is installed and JAVA_HOME is set correctly.

### Issue: Stock prices not updating
**Solution**: Check if Finnhub API key is configured. The app uses mock data if API is unavailable.

### Issue: Maven build fails
**Solution**: Run `mvn clean install -U` to force update dependencies.

## 📝 Notes for Students

1. **Database**: Uses H2 in-memory database. Data is reset on each restart.
2. **Security**: Password hashing is not implemented for simplicity. In production, use BCrypt.
3. **Authentication**: No JWT/OAuth implemented. Focus is on core trading logic.
4. **Mock Data**: If Finnhub API key is not configured, the app uses predefined mock stock prices.
5. **Initial Balance**: Each user starts with $100,000 virtual money.

## 🤝 Contributing

This is an educational project. Students are encouraged to:
- Add new features (watchlist, alerts, etc.)
- Implement authentication
- Add unit tests
- Create a frontend using React/Angular

## 📄 License

This project is licensed under the Apache License 2.0.

## 📧 Contact

For questions or issues, please contact: support@tradingplatform.com

---

**Happy Learning! 🎉**
