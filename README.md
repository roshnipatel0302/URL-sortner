# URL Shortener API

A production-ready URL Shortener service built with Spring Boot and PostgreSQL. Similar to Bitly or TinyURL.

## Features
- ✅ Shorten long URLs into a unique 6-character alphanumeric code.
- ✅ Redirect users from short URL to original URL.
- ✅ Track click counts for each URL.
- ✅ URL validation and error handling.
- ✅ Production-ready structure (Controller, Service, Repository, DTO).

## Tech Stack
- **Java 17**
- **Spring Boot 3.2.2**
- **Spring Data JPA**
- **PostgreSQL**
- **Maven**
- **Lombok**

## Getting Started Locally

### 1. Prerequisites
- Java 17 installed.
- PostgreSQL installed and running.
- Maven installed.

### 2. Database Setup
Create a database named `urlshortener` in your PostgreSQL:
```sql
CREATE DATABASE urlshortener;
```

### 3. Configuration
Update `src/main/resources/application.properties` with your PostgreSQL credentials:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/urlshortener
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 4. Run the Application
```bash
mvn spring-boot:run
```

## API Endpoints

### 1. Shorten URL
- **Endpoint:** `POST /api/shorten`
- **Body:**
```json
{
  "longUrl": "https://www.google.com"
}
```
- **Response:**
```json
{
  "originalUrl": "https://www.google.com",
  "shortUrl": "http://localhost:8080/aB34ef",
  "shortCode": "aB34ef",
  "createdAt": "2024-03-20T10:00:00",
  "clickCount": 0
}
```

### 2. Redirect
- **Endpoint:** `GET /{shortCode}`
- **Action:** Redirects to the original URL and increments click count.

---

## Deployment to Railway

### 1. Create a Railway Project
1. Log in to [Railway.app](https://railway.app/).
2. Click **New Project** -> **Provision PostgreSQL**.

### 2. Add Your Application
1. Click **New** -> **GitHub Repo** (if your code is on GitHub) or **Empty Service**.
2. If uploading manually, you can use the Railway CLI: `railway up`.

### 3. Configure Environment Variables
In the Railway dashboard for your application service, add the following variables:
- `SPRING_PROFILES_ACTIVE`: `prod`
- `DATABASE_URL`: (Railway usually provides this automatically if you connect the DB)
- `DB_USERNAME`: (From PostgreSQL variables)
- `DB_PASSWORD`: (From PostgreSQL variables)

*Note: Since our `application-prod.properties` uses these, ensure they match.*

### 4. Test the Live API
Once deployed, Railway will provide a public URL like `https://url-shortener-production.up.railway.app`.

1. Use Postman or `curl` to send a POST request:
   ```bash
   curl -X POST https://your-app-name.up.railway.app/api/shorten \
   -H "Content-Type: application/json" \
   -d '{"longUrl": "https://github.com"}'
   ```
2. Open the returned `shortUrl` in your browser.

## Project Structure
- `controller`: Entry points for API.
- `service`: Business logic (code generation, DB interaction).
- `repository`: JPA interface for DB operations.
- `model`: JPA entities (Database schema).
- `dto`: Data Transfer Objects for API requests/responses.
- `exception`: Global error handling.
