package com.bank.rag.controller;

import com.bank.rag.dto.BankingDTOs.*;
import com.bank.rag.entity.BankingDocument;
import com.bank.rag.service.RagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "Enterprise Banking RAG APIs")
public class RagController {

    private final RagService ragService;

    @GetMapping("/health")
    @Operation(summary = "1. Health Check API", description = "Verifies the application health and readiness.")
    public Map<String, String> health() {
        return Map.of("status", "UP", "system", "Enterprise Banking RAG Engine");
    }

    @PostMapping(value = "/documents/upload", consumes = "multipart/form-data")
    @Operation(summary = "2. Upload & Index Document (PDF/TXT)", description = "Parses file, creates text chunks, generates embeddings, and stores in pgvector.")
    public ResponseEntity<String> uploadDocument(@RequestParam("file") MultipartFile file) {
        ragService.indexDocument(file);
        return ResponseEntity.ok("Document uploaded, embedded, and stored in pgvector successfully.");
    }

    @GetMapping("/documents")
    @Operation(summary = "3. List Indexed Documents", description = "Retrieves all policy documents currently indexed in the knowledge base.")
    public List<BankingDocument> getDocuments() {
        return ragService.getAllDocuments();
    }

    @PostMapping("/chat/rag")
    @Operation(summary = "4. RAG Chat API (Banking Knowledge Base)", description = "Searches pgvector for context and answers user queries strictly based on indexed policy docs.")
    public ChatResp chatRag(@RequestBody ChatReq req) {
        return ragService.chatRag(req);
    }

    @PostMapping("/chat/search")
    @Operation(summary = "5. Semantic Vector Search API", description = "Performs raw similarity search in pgvector and returns top matching document text chunks.")
    public List<String> semanticSearch(@RequestBody ChatReq req) {
        return ragService.search(req.getQuery());
    }

    @PostMapping("/chat")
    @Operation(summary = "6. Direct GenAI Chat API (No RAG Context)", description = "Directly queries the LLM without retrieving private document context.")
    public ChatResp directChat(@RequestBody ChatReq req) {
        return ragService.chat(req);
    }
}
