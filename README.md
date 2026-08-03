# Enterprise Banking RAG + GenAI Application

Welcome! This repository contains a production-ready Retrieval-Augmented Generation (RAG) application built with Spring Boot 3.4, Spring AI, Java 21, and PostgreSQL (pgvector).

## What is RAG? (Explained for Beginners)

Standard AI models (like ChatGPT) do not know your private banking documents. RAG solves this:
1. Upload Document: You upload a Banking Policy PDF.
2. Chunk & Embed: Converts text chunks into mathematical vectors.
3. Save in pgvector: Vector embeddings stored in PostgreSQL.
4. Search & Answer: When a user asks a question, the system searches pgvector for relevant chunks and provides them to the AI model to construct a factual answer with source citations.

## Configuration & Placeholders in application.yml
- AI_URL: Your OpenAI-compatible API base URL
- AI_PASSWORD: Your API Key
- AI_MODEL: Chat model name
- AI_EMBEDDING_MODEL: Embedding model name

## Quick Start
1. Run Postgres Vector DB:
   docker-compose up -d

2. Build & Run Application:
   mvn clean package -DskipTests
   mvn spring-boot:run

3. Swagger UI: http://localhost:8089/swagger-ui.html

## APIs Overview
1. GET /health - Application status
2. POST /documents/upload - Upload & index PDF/TXT
3. GET /documents - List indexed docs
4. POST /chat/rag - RAG-based context query
5. POST /chat/search - Raw vector search
6. POST /chat - Direct LLM query