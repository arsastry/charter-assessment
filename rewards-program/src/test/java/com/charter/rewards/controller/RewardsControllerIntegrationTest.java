package com.charter.rewards.controller;

import com.charter.rewards.model.Transaction;
import com.charter.rewards.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for {@link RewardsController}.
 */
@SpringBootTest
@AutoConfigureMockMvc
class RewardsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();

        LocalDate now = LocalDate.now();
        LocalDate oneMonthAgo = now.minusMonths(1);
        LocalDate twoMonthsAgo = now.minusMonths(2);

        List<Transaction> transactions = List.of(
                // Customer 1 - multiple transactions across months
                new Transaction(1L, "Alice", 120.0, twoMonthsAgo.withDayOfMonth(5)),   // 90
                new Transaction(1L, "Alice", 75.0, oneMonthAgo.withDayOfMonth(10)),      // 25
                new Transaction(1L, "Alice", 200.0, now.withDayOfMonth(1)),              // 250

                // Customer 2
                new Transaction(2L, "Bob", 150.0, oneMonthAgo.withDayOfMonth(3)),        // 150
                new Transaction(2L, "Bob", 45.0, now.withDayOfMonth(2)),                  // 0
                new Transaction(2L, "Bob", 300.0, now.withDayOfMonth(4))                  // 450
        );

        transactionRepository.saveAll(transactions);
    }

    @Test
    @DisplayName("GET /api/rewards/{customerId} - returns rewards for a valid customer")
    void getRewardsByCustomerId_validCustomer() throws Exception {
        mockMvc.perform(get("/api/rewards/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.customerId").value(1))
                .andExpect(jsonPath("$.customerName").value("Alice"))
                .andExpect(jsonPath("$.totalPoints").value(365))
                .andExpect(jsonPath("$.monthlyPoints").isMap());
    }

    @Test
    @DisplayName("GET /api/rewards/{customerId} - returns 404 for non-existent customer")
    void getRewardsByCustomerId_notFound() throws Exception {
        mockMvc.perform(get("/api/rewards/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("999")));
    }

    @Test
    @DisplayName("GET /api/rewards - returns rewards for all customers")
    void getAllRewards() throws Exception {
        mockMvc.perform(get("/api/rewards"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].customerId", containsInAnyOrder(1, 2)));
    }

    @Test
    @DisplayName("GET /api/rewards/{customerId} - customer with $45 transaction gets 0 points for that month")
    void getRewardsByCustomerId_belowThreshold() throws Exception {
        mockMvc.perform(get("/api/rewards/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPoints").value(600));
    }

    @Test
    @DisplayName("GET /api/rewards - empty database returns empty list")
    void getAllRewards_emptyDatabase() throws Exception {
        transactionRepository.deleteAll();

        mockMvc.perform(get("/api/rewards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}

