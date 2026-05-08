package com.charter.rewards.exception;

/**
 * Exception thrown when a requested customer is not found in the system.
 */
public class CustomerNotFoundException extends RuntimeException {

    /**
     * Constructs a new CustomerNotFoundException with the specified detail message.
     *
     * @param message the detail message
     */
    public CustomerNotFoundException(String message) {
        super(message);
    }
}

