package com.bank.rag.repository;

import com.bank.rag.entity.BankingDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepo extends JpaRepository<BankingDocument, Long> {}
