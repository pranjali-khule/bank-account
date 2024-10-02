package com.banksystem.service;

import com.banksystem.audit.AuditSystem;
import com.banksystem.model.Batch;
import com.banksystem.model.Submission;
import com.banksystem.model.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BankAccountServiceImpl implements BankAccountService {
    private final static double MAX_AMOUNT_PER_BATCH = 1000000;
    private final static long MAX_NUMBER_OF_TRANSACTIONS_PER_SUBMISSION = 1000;

    private int totalTransactionsCount;
    private double currentBalance;
    private Submission submission;
    private AuditSystem auditSystem;

    public BankAccountServiceImpl(AuditSystem auditSystem){
        this.auditSystem = auditSystem;
        this.submission = new Submission();
    }

    @Override
    public synchronized void processTransaction(Transaction transaction) {
            double newBalance = currentBalance + transaction.getAmount();
            if (newBalance < 0) {
                log.warn("Insufficient funds Current Balance: {}, Transaction Amount: {}",
                        currentBalance, transaction.getAmount());
                return;
            }
            currentBalance = newBalance;
            //Add transaction to a batch
            addToBatch(transaction);
    }

    private void addToBatch(Transaction transaction) {
        double transactionAmount = Math.abs(transaction.getAmount());
        addToBatch(transactionAmount);
        checkAndSubmitToAuditSystem();
    }

    private boolean addToBatch(double transactionAmount) {
        totalTransactionsCount ++;
        Batch batch = submission.getBatches().peek();
        if (null != batch && batch.canAdd(transactionAmount, MAX_AMOUNT_PER_BATCH)) {
            submission.getBatches().remove(batch);
            batch.addTransaction(transactionAmount);
            submission.getBatches().add(batch);
            return true;
        }
        Batch newBatch = new Batch();
        newBatch.addTransaction(transactionAmount);
        submission.getBatches().add(newBatch);
        return false;
    }

    private void checkAndSubmitToAuditSystem() {
        if (totalTransactionsCount == MAX_NUMBER_OF_TRANSACTIONS_PER_SUBMISSION) {
            auditSystem.submit(submission);
            submission = new Submission();
            totalTransactionsCount = 0;
        }
    }

    @Override
    public double retrieveBalance() {
        return currentBalance;
    }
}
