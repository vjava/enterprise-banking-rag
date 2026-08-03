package com.bank.rag.service.impl;

import com.bank.rag.dto.BankingDTOs.*;
import com.bank.rag.entity.BankingDocument;
import com.bank.rag.repository.DocumentRepo;
import com.bank.rag.service.RagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RagServiceImpl implements RagService {
    private final VectorStore vectorStore;
    private final ChatClient.Builder chatClientBuilder;
    private final DocumentRepo documentRepo;

    @Value("classpath:/prompts/rag-prompt.st")
    private Resource ragPromptResource;

    @Override
    public void indexDocument(MultipartFile file) {
        log.info("RAG Step 1: Receiving document upload - {}", file.getOriginalFilename());
        try {
            BankingDocument doc = documentRepo.save(BankingDocument.builder()
                    .filename(file.getOriginalFilename())
                    .status("PROCESSING")
                    .build());

            String text = "";
            if (file.getOriginalFilename() != null && file.getOriginalFilename().endsWith(".pdf")) {
                try (PDDocument pd = Loader.loadPDF(file.getBytes())) {
                    text = new PDFTextStripper().getText(pd);
                }
            } else {
                text = new String(file.getBytes());
            }

            log.info("RAG Step 2 & 3: Chunking text with TokenTextSplitter");
            List<Document> documents = new TokenTextSplitter().split(new Document(text));
            documents.forEach(d -> d.getMetadata().put("source", file.getOriginalFilename()));

            log.info("RAG Step 4 & 5: Generating embeddings and saving {} chunks to pgvector", documents.size());
            vectorStore.add(documents);

            doc.setStatus("INDEXED");
            documentRepo.save(doc);
            log.info("Document successfully indexed into vector database.");
        } catch (Exception e) {
            log.error("Failed to process document", e);
            throw new RuntimeException("Document processing error", e);
        }
    }

    @Override
    public List<BankingDocument> getAllDocuments() {
        return documentRepo.findAll();
    }

    @Override
    public ChatResp chat(ChatReq req) {
        log.info("Direct Chat Request without RAG");
        String answer = chatClientBuilder.build().prompt(req.getQuery()).call().content();
        ChatResp resp = new ChatResp();
        resp.setAnswer(answer);
        return resp;
    }

    @Override
    public ChatResp chatRag(ChatReq req) {
        log.info("RAG Flow Triggered for Query: {}", req.getQuery());

        log.info("RAG Step 6 & 7: Generating Query Embedding & Similarity Search in pgvector");

        // Correct Spring AI 1.0.0-M4 API usage: SearchRequest.query(String).withTopK(int)
        SearchRequest searchRequest = SearchRequest.query(req.getQuery()).withTopK(4);

        List<Document> similarDocs = vectorStore.similaritySearch(searchRequest);

        // Correct Spring AI 1.0.0-M4 API usage: Document::getFormattedContent
        String context = similarDocs.stream()
                .map(Document::getFormattedContent)
                .collect(Collectors.joining("\n\n"));

        List<String> sources = similarDocs.stream()
                .map(d -> (String) d.getMetadata().get("source"))
                .distinct()
                .toList();

        log.info("RAG Step 8 & 9: Injecting Context into System Prompt");
        PromptTemplate template = new PromptTemplate(ragPromptResource);
        org.springframework.ai.chat.prompt.Prompt prompt = template.create(Map.of("context", context, "question", req.getQuery()));

        log.info("RAG Step 10: Calling LLM Model with Augmented Prompt");
        String answer = chatClientBuilder.build().prompt(prompt).call().content();

        ChatResp resp = new ChatResp();
        resp.setAnswer(answer);
        resp.setSources(sources);
        return resp;
    }

    @Override
    public List<String> search(String query) {
        log.info("Semantic Vector Search for Query: {}", query);

        SearchRequest searchRequest = SearchRequest.query(query).withTopK(4);

        return vectorStore.similaritySearch(searchRequest)
                .stream()
                .map(Document::getFormattedContent)
                .toList();
    }
}