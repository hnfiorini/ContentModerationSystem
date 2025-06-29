# User Flag Application

This is a basic content moderation system that processes a CSV file with user messages, scores them using external services, and generates an output report with offensive scores per user.

## 🚀 Technologies Used

- Java 17
- Spring Boot 3.4.1
- Caffeine (for caching)
- Apache Commons CSV
- Maven
- JUnit 5 + Mockito

## 🧠 Problem Summary & Approach

The challenge was to process a potentially large CSV file and interact with two external APIs: a **translation** service and a **scoring** service. These services can have latency and are **idempotent**, meaning the same message will always return the same result.

Key decisions:
- **Streaming CSV reading**: The input CSV is processed line by line to support millions of records efficiently.
- **Parallel execution**: Messages are processed using a thread pool to reduce the impact of external service latency.
- **Caching**: Messages are cached using Caffeine to avoid duplicate calls to translation and scoring services.

## 🧪 How to Run and Test

1. **Clone the project**

```bash
git clone https://github.com/hnfiorini/ContentModerationSystem.git
cd user-flag-app
```

2. **Build with Maven**

```bash
mvn clean install
```

3. **Run the app**

```bash
mvn spring-boot:run
```

4. **Test via browser or Postman**

You can hit the following endpoint:

```http
GET http://localhost:8080/process
```

By default, it uses a sample CSV file included in `src/main/resources/input.csv`.

You can also pass your own files with full paths (Windows-style example):

```http
GET http://localhost:8080/process?inputFileName=C:/Users/{YourUser}/Desktop/test.csv&outputFileName=C:/Users/{YourUser}/Desktop/result.csv
```

## 📂 Input CSV Format

```
user_id,message
user_1,This is a test message
user_1,Another one
user_2,Hola mundo
```

## 📄 Output CSV Format

```
user_id,total_messages,avg_score
user_1,2,0.5
user_2,1,0.4
```

## ⚠️ Pending Improvements

- Add more unit and integration tests
- Improve logging and error messages
- Better error responses for invalid CSV formats
- Validate input messages (e.g. trim, sanitize)
- Add Swagger/OpenAPI for endpoint documentation

---

