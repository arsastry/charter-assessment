package com.charter.rewards.dto;

import java.util.Map;

/**
 * Data Transfer Object representing reward points for a single customer.
 *
 * @param customerId   the unique identifier of the customer
 * @param customerName the name of the customer
 * @param monthlyPoints a map of month to points earned that month
 * @param totalPoints  the total reward points earned across all months
 */
public record CustomerRewardsResponse(
        Long customerId,
        String customerName,
        Map<String, Integer> monthlyPoints,
        int totalPoints
) {
}

