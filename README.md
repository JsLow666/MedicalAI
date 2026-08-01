# Xiaozhi Medical AI

Xiaozhi Medical AI is a Spring Boot backend for a hospital-style AI assistant. It provides streamed, multi-turn conversations for general health guidance, department navigation, and appointment workflows.

The application uses LangChain4j with DashScope/Qwen models, MongoDB chat memory, Pinecone retrieval-augmented generation (RAG), and MySQL-backed appointment records.

> **Medical disclaimer:** This project is a demonstration assistant, not a diagnostic or prescription system. It must not replace a qualified healthcare professional or emergency service. For urgent or life-threatening symptoms, contact local emergency services or seek immediate in-person care.

## Features

- Streamed AI chat responses via a REST API
- Multi-turn conversation memory stored in MongoDB
- RAG over a Pinecone vector index for hospital and medical knowledge
- Appointment availability checking, booking, and cancellation tools
- MySQL persistence for appointment records through MyBatis-Plus
- OpenAPI documentation provided by Knife4j

## Architecture

```text
Client
  └─ POST /xiaozhi/chat
       └─ XiaozhiAgent (LangChain4j AI service)
            ├─ DashScope Qwen streaming model
            ├─ MongoDB chat-memory store
            ├─ Pinecone content retriever
            └─ Appointment tools → MySQL
```

The agent is instructed to collect the required appointment information—name, ID card number, department, date, time, and optionally doctor name—before booking or cancelling an appointment.

## Technology Stack

- Java 17
- Spring Boot 3.5
- LangChain4j
- DashScope / Qwen
- MongoDB
- MySQL and MyBatis-Plus
- Pinecone
- Project Reactor / WebFlux
- Knife4j / OpenAPI

## Prerequisites

Before starting the application, make sure the following services and credentials are available:

- JDK 17 or newer
- MongoDB running locally on port `27017`
- MySQL running locally on port `3306`
- A Pinecone API key and access to the `xiaozhi-agent` index
- A DashScope API key

Ollama settings are also present in the configuration, but the current agent is wired to use the DashScope Qwen streaming model.

## Configuration

Set these environment variables before running the application:

```powershell
$env:DASHSCOPE_API_KEY = "your-dashscope-api-key"
$env:PINECONE_API_KEY = "your-pinecone-api-key"
$env:MYSQL_PASSWORD = "your-mysql-password"
```

The default local service configuration is in [`src/main/resources/application.properties`](src/main/resources/application.properties):

```properties
spring.data.mongodb.uri=mongodb://localhost:27017/chat_memory_db
spring.datasource.url=jdbc:mysql://localhost:3306/guiguxiaozhi
spring.datasource.username=root
server.port=8080
```

Adjust these values for your environment. The Pinecone index and namespace are currently configured in `EmbeddingStoreConfig` as `xiaozhi-agent` and `xiaozhi-namespace`.

## Database Setup

Create the configured MySQL database and an `appointment` table before using appointment features:

```sql
CREATE DATABASE IF NOT EXISTS xiaozhi;
USE xiaozhi;

CREATE TABLE appointment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    id_card VARCHAR(50) NOT NULL,
    department VARCHAR(100) NOT NULL,
    date VARCHAR(20) NOT NULL,
    time VARCHAR(20) NOT NULL,
    doctor_name VARCHAR(100)
);
```

## Run Locally

Use the Maven wrapper included with the project:

```powershell
.\mvnw.cmd spring-boot:run
```

The server starts at `http://localhost:8080` by default.

To run tests:

```powershell
.\mvnw.cmd test
```

## API

### Chat

`POST /xiaozhi/chat`

The endpoint returns a streaming `text/stream` response. Send a stable `memoryId` to keep a conversation context across requests.

```json
{
  "memoryId": "demo-user-001",
  "message": "I have had a cough for three days. Which department should I visit?"
}
```

Example with `curl`:

```bash
curl -N -X POST http://localhost:8080/xiaozhi/chat \
  -H "Content-Type: application/json" \
  -d '{"memoryId":"demo-user-001","message":"I want to make an appointment with the cardiology department."}'
```

OpenAPI documentation is exposed by the Knife4j dependency when the application is running. Check the generated endpoint in your local setup, commonly `http://localhost:8080/doc.html`.

## Knowledge Base

Reference material is available in [`src/main/resources/knowledgeBase`](src/main/resources/knowledgeBase). The active runtime retriever queries Pinecone, so documents must be embedded and uploaded to the configured Pinecone index before they can be returned during a chat.

## Project Structure

```text
src/main/java/com/junsheng/XiaozhiAI/
├─ assistant/       # LangChain4j AI-service interface
├─ controller/      # REST endpoints
├─ config/          # Chat-memory, RAG, and Pinecone configuration
├─ store/           # MongoDB chat-memory implementation
├─ tools/           # Appointment tools exposed to the model
├─ service/         # Appointment business service
└─ mapper/          # MyBatis-Plus mapper

src/main/resources/
├─ systemPrompt.txt  # Assistant behavior and workflow rules
├─ knowledgeBase/    # Source knowledge documents
└─ application.properties
```

