package com.charter.rewards.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

/**
 * Entity representing a customer purchase transaction.
 * <p>
 * Each transaction records the customer who made the purchase,
 * the amount spent, and the date of the transaction.
 * </p>
 */
@Entity
@Table(name = "transactions")
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

    /** Default constructor required by JPA. */
    public Transaction() {
    }

    /**
     * Constructs a new Transaction with the specified details.
     *
     * @param customerId      the customer identifier
     * @param customerName    the customer name
     * @param amount          the transaction amount in dollars
     * @param transactionDate the date of the transaction
     */
    public Transaction(Long customerId, String customerName, Double amount, LocalDate transactionDate) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.amount = amount;
        this.transactionDate = transactionDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }
}

