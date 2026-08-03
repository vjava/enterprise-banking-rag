package com.bank.rag.controller;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class BeanChecker implements CommandLineRunner {

    private final EmbeddingModel embeddingModel;

    public BeanChecker(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    @Override
    public void run(String... args) {
        System.out.println("========================================");
        System.out.println("Embedding Bean : " + embeddingModel.getClass().getName());
        System.out.println("========================================");
    }
}
