package com.charter.rewards.dto;

import java.util.Map;

/**
 * Data Transfer Object representing reward points for a single customer.
 * <p>
 * Contains the customer's identifying information, a breakdown of
 * reward points earned per month, and the total points across all months.
 * </p>
 *
 * @param customerId   the unique identifier of the customer
 * @param customerName the name of the customer
 * @param monthlyPoints a map of month (e.g. "2026-02") to points earned that month
 * @param totalPoints  the total reward points earned across all months
 */
public record CustomerRewardsResponse(
        Long customerId,
        String customerName,
        Map<String, Integer> monthlyPoints,
        int totalPoints
) {
}

