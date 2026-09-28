# Banking AI Assistant

A complete full-stack AI-powered banking assistant where authenticated customers can ask natural-language questions about their account balance, details, transactions, and statements.

The AI uses **Spring AI** with controlled `@Tool` functions — it **never** accesses MySQL directly or executes arbitrary SQL.

## Architecture

```
React (Vite)
    ↓  REST + JWT
Spring Boot REST API
    ↓
JWT Authentication (Spring Security)
    ↓
Spring AI ChatClient
    ↓
Controlled Banking Tools (@Tool)
    ↓
Banking Services
    ↓
JPA Repositories
    ↓
MySQL
```

## Technologies

| Layer      | Stack                                      |
|------------|--------------------------------------------|
| Backend    | Java 17, Spring Boot 3.3, Spring AI, Spring Security, JPA, JWT |
| Database   | MySQL 8                                    |
| Frontend   | React 18, Vite, Axios, React Router        |
| AI         | OpenAI (gpt-4o-mini) via Spring AI         |
| Build      | Maven (backend), npm (frontend)            |

## Prerequisites

- Java 17+
- Maven 3.8+
- Node.js 18+
- MySQL 8
- OpenAI API key

## 1. MySQL Setup

```bash
mysql -u root -p
```

```sql
CREATE DATABASE IF NOT EXISTS banking_ai;
```

Or rely on `createDatabaseIfNotExist=true` in the JDBC URL.

## 2. Environment Variables

Copy the example and fill in your values:

```bash
cp .env.example .env
# or export them in your shell
```

```bash
export DB_URL="jdbc:mysql://localhost:3306/banking_ai?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
export DB_USERNAME=root
export DB_PASSWORD=your_password
export JWT_SECRET="banking-ai-jwt-secret-key-must-be-at-least-256-bits-long-for-hs256"
export OPENAI_API_KEY="sk-your-openai-api-key"
```

## 3. Backend Startup

```bash
cd banking-ai-backend
mvn clean spring-boot:run
```

Backend runs at **http://localhost:8080**

On first start, sample data is automatically inserted:
- Customer: `Dinesh` / `customer@example.com` / `password`
- Account: `100012345678` (SAVINGS) — balance ₹45,250
- 10+ sample transactions for September 2026

## 4. Frontend Startup

```bash
cd banking-ai-frontend
npm install
npm run dev
```

Frontend runs at **http://localhost:5173**

## 5. Sample Login

| Field    | Value                  |
|----------|------------------------|
| Email    | customer@example.com   |
| Password | password               |

## 6. Sample AI Questions

After logging in, open **AI Assistant** and try:

- `What is my account balance?`
- `Show my account details`
- `Show my last 5 transactions`
- `Give me my statement for September`
- `Show transactions between 1 September and 15 September`

## 7. API Endpoints

### Auth
| Method | Path                 | Auth | Description        |
|--------|----------------------|------|--------------------|
| POST   | /api/auth/register   | No   | Register customer  |
| POST   | /api/auth/login      | No   | Login, get JWT     |
| GET    | /api/auth/me         | Yes  | Current user       |

### Accounts (all require JWT)
| Method | Path                              | Description              |
|--------|-----------------------------------|--------------------------|
| GET    | /api/accounts                     | List accounts            |
| GET    | /api/accounts/balance             | Current balance          |
| GET    | /api/accounts/details             | Account details          |
| GET    | /api/accounts/transactions/recent | Recent transactions      |
| GET    | /api/accounts/statement?from=&to= | Statement by date range  |

### AI
| Method | Path           | Description              |
|--------|----------------|--------------------------|
| POST   | /api/ai/chat   | Natural language chat    |

## 8. Security Design

- Passwords hashed with **BCrypt**
- **JWT** (HS256) authentication — no session state
- Customer ID taken **only** from the JWT / SecurityContext — never from the request body or AI
- Account ownership validation in services
- Input validation with Jakarta Validation
- Global exception handler with consistent error JSON
- CORS restricted to frontend origins
- No passwords, JWT secrets, or full account numbers in API responses
- AI tools are controlled `@Tool` methods — no arbitrary SQL

## 9. Postman Collection

Import `postman/Banking_AI_Postman_Collection.json`.

1. Run **Login** first (token is auto-saved)
2. Then call any authenticated endpoint or AI chat

## 10. Project Structure

```
banking-ai-backend/
  src/main/java/com/example/banking/
    ├── ai/           # AIService, AIController, BankingTools
    ├── config/       # DataInitializer
    ├── controller/   # AuthController, AccountController
    ├── dto/
    ├── entity/
    ├── exception/
    ├── repository/
    ├── security/     # JWT, SecurityConfig, SecurityService
    ├── service/
    └── BankingApplication.java

banking-ai-frontend/
  src/
    ├── components/   # Layout
    ├── context/      # AuthContext
    ├── pages/        # Login, Dashboard, Account, Transactions, Statement, AIAssistant
    ├── services/     # Axios API client
    ├── App.jsx
    └── main.jsx
```

## 11. Exact Commands to Run

```bash
# Terminal 1 – MySQL must be running

# Terminal 2 – Backend
cd banking-ai-backend
export DB_PASSWORD=your_password
export OPENAI_API_KEY=sk-...
mvn clean spring-boot:run

# Terminal 3 – Frontend
cd banking-ai-frontend
npm install
npm run dev
```

Open http://localhost:5173 → Login → AI Assistant → ask *"What is my balance?"*

## Screenshots

*(Add screenshots of Login, Dashboard, and AI Assistant here)*

---

**License:** MIT
