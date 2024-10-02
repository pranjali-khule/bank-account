package com.banksystem.service;

import java.util.HashMap;
import java.util.Map;

/**
 * Service to aggregate transactions tracking the overall balance for each account.
 * <p>
 * This service is expected to receive a significant number of transactions potentially from multiple threads.
 */
public class TransactionAggregationService {
    private final Map<String, Float> map = new HashMap<>(); // Map of Account Number to Balance

    /**
     * Process a transaction
     *
     * @param accountNumber the account number the transaction relates to
     * @param transactionAmount the amount of the transaction (positive for credits, negative for debits)
     */
    public void processTransaction(String accountNumber, Float transactionAmount) throws Throwable {

        if (!accountNumber.startsWith("ACC")) {
            throw new Throwable("Account number invalid");
        } else if (accountNumber.length() != 8) {
            throw new Throwable("Account number invalid");
        } else if (transactionAmount > Float.MAX_VALUE) {
            throw new Throwable("Transaction amount invalid");
        }
        if (map.get(accountNumber) != null) {
            Float oldValue = map.get(accountNumber);
            map.put(accountNumber, oldValue + transactionAmount);
        } else {
            map.put(accountNumber, transactionAmount);
        }
    }
    public Float getBalance(String account) {
        return map.getOrDefault(account, -1F);
    }
}