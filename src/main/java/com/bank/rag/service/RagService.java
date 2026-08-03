package com.bank.rag.service;

import com.bank.rag.dto.BankingDTOs.*;
import com.bank.rag.entity.BankingDocument;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface RagService {
    void indexDocument(MultipartFile file);
    List<BankingDocument> getAllDocuments();
    ChatResp chat(ChatReq req);
    ChatResp chatRag(ChatReq req);
    List<String> search(String query);
}
