package com.charter.rewards;

import com.charter.rewards.model.Transaction;
import com.charter.rewards.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Loads sample transaction data into the database on application startup.
 */
@Component
@RequiredArgsConstructor
public class DataLoader {

    private final TransactionRepository transactionRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void initializeDatabaseWithMockData() {
        LocalDate now = LocalDate.now();
        LocalDate oneMonthAgo = now.minusMonths(1);
        LocalDate twoMonthsAgo = now.minusMonths(2);

        List<Transaction> transactions = List.of(
                // Customer 1 - Alice: transactions across 3 months
                new Transaction(1L, "John Doe", 120.00, twoMonthsAgo.withDayOfMonth(5)),
                new Transaction(1L, "John Doe", 75.00, twoMonthsAgo.withDayOfMonth(15)),
                new Transaction(1L, "John Doe", 200.00, oneMonthAgo.withDayOfMonth(10)),
                new Transaction(1L, "John Doe", 50.00, oneMonthAgo.withDayOfMonth(20)),
                new Transaction(1L, "John Doe", 110.00, now.withDayOfMonth(1)),

                // Customer 2 - Bob: transactions across 3 months
                new Transaction(2L, "Suresh Sharma", 150.00, twoMonthsAgo.withDayOfMonth(3)),
                new Transaction(2L, "Suresh Sharma", 45.00, twoMonthsAgo.withDayOfMonth(20)),
                new Transaction(2L, "Suresh Sharma", 300.00, oneMonthAgo.withDayOfMonth(8)),
                new Transaction(2L, "Suresh Sharma", 60.00, now.withDayOfMonth(2)),

                // Customer 3 - Charlie: transactions across 2 months
                new Transaction(3L, "Vanita Shetty", 90.00, oneMonthAgo.withDayOfMonth(12)),
                new Transaction(3L, "Vanita Shetty", 55.00, now.withDayOfMonth(3)),
                new Transaction(3L, "Vanita Shetty", 180.00, now.withDayOfMonth(5))
        );

        transactionRepository.saveAll(transactions);
    }
}

