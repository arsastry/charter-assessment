package com.charter.rewards.controller;

import com.charter.rewards.dto.CustomerRewardsResponse;
import com.charter.rewards.service.RewardsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller exposing endpoints for querying customer reward points.
 * <p>
 * Provides endpoints to retrieve reward points for a specific customer
 * or for all customers, calculated over the last three months of transactions.
 * </p>
 */
@RestController
@RequestMapping("/api/rewards")
@RequiredArgsConstructor
public class RewardsController {

    private final RewardsService rewardsService;

    /**
     * Retrieves reward points for a specific customer.
     *
     * @param customerId the unique identifier of the customer
     * @return the customer's reward points summary
     */
    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerRewardsResponse> getRewardsByCustomerId(@PathVariable Long customerId) {
        CustomerRewardsResponse response = rewardsService.getRewardsForCustomer(customerId);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves reward points for all customers.
     *
     * @return list of reward points summaries for all customers
     */
    @GetMapping
    public ResponseEntity<List<CustomerRewardsResponse>> getAllRewards() {
        List<CustomerRewardsResponse> responses = rewardsService.getRewardsForAllCustomers();
        return ResponseEntity.ok(responses);
    }
}

