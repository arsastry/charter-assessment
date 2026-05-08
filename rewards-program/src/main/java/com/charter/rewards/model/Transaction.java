package com.charter.rewards.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;

/**
 * Entity representing a customer purchase transaction.
 */
@Entity
@Table(name = "transactions")
@NoArgsConstructor
@Getter
@Setter
public class Transaction {

    /** Unique identifier for the transaction. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Identifier of the customer who made the transaction. */
    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    /** Name of the customer. */
    @Column(name = "customer_name", nullable = false)
    private String customerName;

    /** Amount spent in the transaction (in dollars). */
    @Column(nullable = false)
    private Double amount;

    /** Date when the transaction occurred. */
    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;

    /** Custom constructor for usage in DataLoader */
    public Transaction(Long customerId, String customerName, Double amount, LocalDate transactionDate) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.amount = amount;
        this.transactionDate = transactionDate;
    }
}

