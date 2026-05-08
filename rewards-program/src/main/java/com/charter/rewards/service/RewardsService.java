package com.charter.rewards.service;

import com.charter.rewards.dto.CustomerRewardsResponse;
import com.charter.rewards.exception.CustomerNotFoundException;
import com.charter.rewards.model.Transaction;
import com.charter.rewards.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service responsible for calculating reward points from customer transactions.
 * <p>
 * Reward rules:
 * <ul>
 *   <li>2 points for every dollar spent over $100 per transaction</li>
 *   <li>1 point for every dollar spent between $50 and $100 per transaction</li>
 * </ul>
 * For example, a $120 purchase earns 2×$20 + 1×$50 = 90 points.
 * </p>
 */
@Service
public class RewardsService {

    private static final int HIGH_THRESHOLD = 100;
    private static final int LOW_THRESHOLD = 50;
    private static final int HIGH_MULTIPLIER = 2;
    private static final int LOW_MULTIPLIER = 1;
    private static final int DEFAULT_MONTHS = 3;
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    private final TransactionRepository transactionRepository;

    /**
     * Constructs the RewardsService with the given transaction repository.
     *
     * @param transactionRepository the repository for transaction data access
     */
    public RewardsService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * Calculates reward points for a specific customer over the last three months.
     *
     * @param customerId the customer identifier
     * @return the customer's reward points broken down by month and total
     * @throws CustomerNotFoundException if no customer with the given ID exists
     */
    public CustomerRewardsResponse getRewardsForCustomer(Long customerId) {
        if (!transactionRepository.existsByCustomerId(customerId)) {
            throw new CustomerNotFoundException("Customer not found with id: " + customerId);
        }

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(DEFAULT_MONTHS).withDayOfMonth(1);

        List<Transaction> transactions = transactionRepository
                .findByCustomerIdAndTransactionDateBetween(customerId, startDate, endDate);

        return buildRewardsResponse(customerId, transactions);
    }

    /**
     * Calculates reward points for all customers over the last three months.
     *
     * @return a list of reward summaries, one per customer
     */
    public List<CustomerRewardsResponse> getRewardsForAllCustomers() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(DEFAULT_MONTHS).withDayOfMonth(1);

        List<Transaction> allTransactions = transactionRepository
                .findByTransactionDateBetween(startDate, endDate);

        Map<Long, List<Transaction>> grouped = allTransactions.stream()
                .collect(Collectors.groupingBy(Transaction::getCustomerId));

        return grouped.entrySet().stream()
                .map(entry -> buildRewardsResponse(entry.getKey(), entry.getValue()))
                .toList();
    }

    /**
     * Calculates reward points for a single transaction amount.
     * <p>
     * Points are calculated as follows:
     * <ul>
     *   <li>0 points for amounts $50 or below</li>
     *   <li>1 point per dollar for the portion between $50 and $100</li>
     *   <li>2 points per dollar for the portion above $100</li>
     * </ul>
     * </p>
     *
     * @param amount the transaction amount in dollars
     * @return the calculated reward points (never negative)
     */
    public int calculatePoints(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Transaction amount cannot be negative: " + amount);
        }

        int intAmount = (int) Math.floor(amount);
        int points = 0;

        if (intAmount > HIGH_THRESHOLD) {
            points += (intAmount - HIGH_THRESHOLD) * HIGH_MULTIPLIER;
            points += (HIGH_THRESHOLD - LOW_THRESHOLD) * LOW_MULTIPLIER;
        } else if (intAmount > LOW_THRESHOLD) {
            points += (intAmount - LOW_THRESHOLD) * LOW_MULTIPLIER;
        }

        return points;
    }

    private CustomerRewardsResponse buildRewardsResponse(Long customerId, List<Transaction> transactions) {
        String customerName = transactions.isEmpty() ? "Unknown" : transactions.get(0).getCustomerName();

        Map<String, Integer> monthlyPoints = new LinkedHashMap<>();
        int totalPoints = 0;

        Map<YearMonth, List<Transaction>> byMonth = transactions.stream()
                .collect(Collectors.groupingBy(t -> YearMonth.from(t.getTransactionDate())));

        // Sort by month
        byMonth.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    int monthPoints = entry.getValue().stream()
                            .mapToInt(t -> calculatePoints(t.getAmount()))
                            .sum();
                    monthlyPoints.put(entry.getKey().format(MONTH_FORMATTER), monthPoints);
                });

        totalPoints = monthlyPoints.values().stream().mapToInt(Integer::intValue).sum();

        return new CustomerRewardsResponse(customerId, customerName, monthlyPoints, totalPoints);
    }
}

