package com.charter.rewards.repository;

import com.charter.rewards.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for accessing {@link Transaction} entities from the database.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Finds all transactions for a given customer within a date range (inclusive).
     *
     * @param customerId the customer identifier
     * @param startDate  the start date (inclusive)
     * @param endDate    the end date (inclusive)
     * @return list of matching transactions
     */
    List<Transaction> findByCustomerIdAndTransactionDateBetween(Long customerId, LocalDate startDate, LocalDate endDate);

    /**
     * Finds all transactions within a date range (inclusive).
     *
     * @param startDate the start date (inclusive)
     * @param endDate   the end date (inclusive)
     * @return list of matching transactions
     */
    List<Transaction> findByTransactionDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Checks if any transactions exist for the given customer.
     *
     * @param customerId the customer identifier
     * @return true if at least one transaction exists
     */
    boolean existsByCustomerId(Long customerId);
}

