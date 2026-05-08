package com.charter.rewards;

import com.charter.rewards.model.Transaction;
import com.charter.rewards.repository.TransactionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Loads sample transaction data into the database on application startup.
 * <p>
 * Dates are calculated dynamically relative to the current date
 * to ensure the data always falls within the last three months.
 * </p>
 */
@Component
public class DataLoader implements CommandLineRunner {

    private final TransactionRepository transactionRepository;

    /**
     * Constructs the DataLoader with the given repository.
     *
     * @param transactionRepository the transaction repository
     */
    public DataLoader(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public void run(String... args) {
        LocalDate now = LocalDate.now();
        LocalDate oneMonthAgo = now.minusMonths(1);
        LocalDate twoMonthsAgo = now.minusMonths(2);

        List<Transaction> transactions = List.of(
                // Customer 1 - Alice: transactions across 3 months
                new Transaction(1L, "Alice Johnson", 120.00, twoMonthsAgo.withDayOfMonth(5)),   // 90 pts
                new Transaction(1L, "Alice Johnson", 75.00, twoMonthsAgo.withDayOfMonth(15)),    // 25 pts
                new Transaction(1L, "Alice Johnson", 200.00, oneMonthAgo.withDayOfMonth(10)),     // 250 pts
                new Transaction(1L, "Alice Johnson", 50.00, oneMonthAgo.withDayOfMonth(20)),      // 0 pts
                new Transaction(1L, "Alice Johnson", 110.00, now.withDayOfMonth(1)),              // 70 pts

                // Customer 2 - Bob: transactions across 3 months
                new Transaction(2L, "Bob Smith", 150.00, twoMonthsAgo.withDayOfMonth(3)),        // 150 pts
                new Transaction(2L, "Bob Smith", 45.00, twoMonthsAgo.withDayOfMonth(20)),         // 0 pts
                new Transaction(2L, "Bob Smith", 300.00, oneMonthAgo.withDayOfMonth(8)),           // 450 pts
                new Transaction(2L, "Bob Smith", 60.00, now.withDayOfMonth(2)),                    // 10 pts

                // Customer 3 - Charlie: transactions across 2 months
                new Transaction(3L, "Charlie Davis", 90.00, oneMonthAgo.withDayOfMonth(12)),     // 40 pts
                new Transaction(3L, "Charlie Davis", 55.00, now.withDayOfMonth(3)),               // 5 pts
                new Transaction(3L, "Charlie Davis", 180.00, now.withDayOfMonth(5))               // 210 pts
        );

        transactionRepository.saveAll(transactions);
    }
}

