package com.bank.rag.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "banking_document")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankingDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String filename;
    private String status;
}
