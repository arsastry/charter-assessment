package com.charter.rewards.service;

import com.charter.rewards.dto.CustomerRewardsResponse;
import com.charter.rewards.exception.CustomerNotFoundException;
import com.charter.rewards.model.Transaction;
import com.charter.rewards.repository.TransactionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link RewardsService}.
 */
@ExtendWith(MockitoExtension.class)
class RewardsServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private RewardsService rewardsService;

    // --- Points calculation tests ---

    @Test
    @DisplayName("$120 purchase should earn 90 points (2×20 + 1×50)")
    void calculatePoints1() {
        assertEquals(90, rewardsService.calculatePoints(120.0));
    }

    @Test
    @DisplayName("$200 purchase should earn 250 points (2×100 + 1×50)")
    void calculatePoints2() {
        assertEquals(250, rewardsService.calculatePoints(200.0));
    }

    @Test
    @DisplayName("$100 purchase should earn 50 points (1×50)")
    void calculatePoints3() {
        assertEquals(50, rewardsService.calculatePoints(100.0));
    }

    @Test
    @DisplayName("$75 purchase should earn 25 points (1×25)")
    void calculatePoints4() {
        assertEquals(25, rewardsService.calculatePoints(75.0));
    }

    @Test
    @DisplayName("$50 purchase should earn 0 points")
    void calculatePoints5() {
        assertEquals(0, rewardsService.calculatePoints(50.0));
    }

    @Test
    @DisplayName("$49.99 purchase should earn 0 points")
    void calculatePoints6() {
        assertEquals(0, rewardsService.calculatePoints(49.99));
    }

    @Test
    @DisplayName("$0 purchase should earn 0 points")
    void calculatePoints7() {
        assertEquals(0, rewardsService.calculatePoints(0.0));
    }

    @Test
    @DisplayName("$51 purchase should earn 1 point")
    void calculatePoints8() {
        assertEquals(1, rewardsService.calculatePoints(51.0));
    }

    @Test
    @DisplayName("$101 purchase should earn 52 points (2×1 + 1×50)")
    void calculatePoints9() {
        assertEquals(52, rewardsService.calculatePoints(101.0));
    }

    // --- Negative / exception scenarios ---

    @Test
    @DisplayName("Negative amount should throw IllegalArgumentException")
    void calculatePoints_negative_shouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> rewardsService.calculatePoints(-10.0));
    }

    @Test
    @DisplayName("Non-existent customer should throw CustomerNotFoundException")
    void getRewardsForCustomer_notFound_shouldThrow() {
        when(transactionRepository.existsByCustomerId(999L)).thenReturn(false);
        assertThrows(CustomerNotFoundException.class, () -> rewardsService.getRewardsForCustomer(999L));
    }

    // --- Customer rewards tests ---

    @Test
    @DisplayName("Should calculate monthly and total points for a customer with multiple transactions")
    void getRewardsForCustomer_multipleTransactions() {
        Long customerId = 1L;
        LocalDate now = LocalDate.now();
        LocalDate oneMonthAgo = now.minusMonths(1);

        List<Transaction> transactions = List.of(
                new Transaction(customerId, "Alice", 120.0, oneMonthAgo.withDayOfMonth(5)),  // 90
                new Transaction(customerId, "Alice", 75.0, oneMonthAgo.withDayOfMonth(15)),   // 25
                new Transaction(customerId, "Alice", 200.0, now.withDayOfMonth(1))             // 250
        );

        when(transactionRepository.existsByCustomerId(customerId)).thenReturn(true);
        when(transactionRepository.findByCustomerIdAndTransactionDateBetween(eq(customerId), any(), any()))
                .thenReturn(transactions);

        CustomerRewardsResponse response = rewardsService.getRewardsForCustomer(customerId);

        assertEquals(customerId, response.customerId());
        assertEquals("Alice", response.customerName());
        assertEquals(365, response.totalPoints());
        assertEquals(2, response.monthlyPoints().size());
    }

    @Test
    @DisplayName("Customer with no transactions in period should return empty monthly points")
    void getRewardsForCustomer_noTransactionsInPeriod() {
        Long customerId = 1L;
        when(transactionRepository.existsByCustomerId(customerId)).thenReturn(true);
        when(transactionRepository.findByCustomerIdAndTransactionDateBetween(eq(customerId), any(), any()))
                .thenReturn(Collections.emptyList());

        CustomerRewardsResponse response = rewardsService.getRewardsForCustomer(customerId);

        assertEquals(0, response.totalPoints());
        assertTrue(response.monthlyPoints().isEmpty());
    }

    @Test
    @DisplayName("Should return rewards for all customers grouped correctly")
    void getRewardsForAllCustomers_multipleCustomers() {
        LocalDate now = LocalDate.now();

        List<Transaction> transactions = List.of(
                new Transaction(1L, "Alice", 120.0, now.withDayOfMonth(1)),    // 90
                new Transaction(2L, "Bob", 150.0, now.withDayOfMonth(2)),       // 150
                new Transaction(1L, "Alice", 200.0, now.withDayOfMonth(3)),    // 250
                new Transaction(2L, "Bob", 60.0, now.withDayOfMonth(4))         // 10
        );

        when(transactionRepository.findByTransactionDateBetween(any(), any())).thenReturn(transactions);

        List<CustomerRewardsResponse> responses = rewardsService.getRewardsForAllCustomers();

        assertEquals(2, responses.size());

        CustomerRewardsResponse alice = responses.stream()
                .filter(r -> r.customerId().equals(1L)).findFirst().orElseThrow();
        assertEquals(340, alice.totalPoints());

        CustomerRewardsResponse bob = responses.stream()
                .filter(r -> r.customerId().equals(2L)).findFirst().orElseThrow();
        assertEquals(160, bob.totalPoints());
    }

    @Test
    @DisplayName("Transactions at exactly $50 boundary should earn 0 points")
    void calculatePoints10() {
        assertEquals(0, rewardsService.calculatePoints(50.0));
    }

    @Test
    @DisplayName("Transactions at exactly $100 boundary should earn 50 points")
    void calculatePoints11() {
        assertEquals(50, rewardsService.calculatePoints(100.0));
    }

    @Test
    @DisplayName("Very large transaction should calculate correctly")
    void calculatePoints12() {
        // $1000: (1000-100)*2 + 50*1 = 1800 + 50 = 1850
        assertEquals(1850, rewardsService.calculatePoints(1000.0));
    }
}

