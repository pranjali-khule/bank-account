package com.banksystem.model;

import java.util.Comparator;
import java.util.PriorityQueue;

import lombok.Data;

@Data
public class Submission {
    private PriorityQueue<Batch> batches;

    public Submission() {
        batches = new PriorityQueue<>(
                Comparator.comparingDouble(Batch::getTotalValueOfAllTransactions)
        );
    }


    @Override
    public String toString() {
        return """
                submission: {
                    batches: %s
                }""".formatted(batches);
    }
}
