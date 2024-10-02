package com.banksystem.model;

import lombok.Data;

@Data
public class Batch {
    private double totalValueOfAllTransactions;
    private long countOfTransactions;

    public Batch() {
        totalValueOfAllTransactions = 0;
        countOfTransactions = 0;
    }

    public boolean canAdd(double transactionAmout, double maxAmountPerBatch) {
        return totalValueOfAllTransactions + transactionAmout <= maxAmountPerBatch;
    }

    public void addTransaction(double amount){
        countOfTransactions++;
        totalValueOfAllTransactions += amount;
    }

    @Override
    public String toString() {
        return """
                \t{
                \t\t totalValueOfAllTransactions: %s
                \t\t countOfTransactions: %s
                \t}
                """.formatted(totalValueOfAllTransactions, countOfTransactions);
    }
}

