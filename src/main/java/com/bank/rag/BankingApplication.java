package com.bank.rag;

import org.springframework.ai.autoconfigure.ollama.OllamaAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        scanBasePackages = "com.bank.rag",
        exclude = { OllamaAutoConfiguration.class }
)
public class BankingApplication {
    public static void main(String[] args) {
        SpringApplication.run(BankingApplication.class, args);
    }
}