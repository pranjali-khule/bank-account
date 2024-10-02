package com.banksystem.producer;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.banksystem.model.Transaction;
import com.banksystem.model.TransactionType;
import com.banksystem.service.BankAccountService;

@Slf4j
@Component
public class TransactionProducerService implements ApplicationRunner {
    private BankAccountService bankAccountService;

    @Value("${transaction.producer_rate}")
    private long period;  // Scheduled the task at a fixed rate of 40 milliseconds (25 times per second)
    @Value("${transaction.min_trx_amount}")
    private double MIN_TRX_AMOUNT;
    @Value("${transaction.max_trx_amount}")
    private double MAX_TRX_AMOUNT;

    private ScheduledExecutorService creditExecutor;
    private ScheduledExecutorService debitExecutor;

    public TransactionProducerService(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Initialising Threads");

        ProducerTask creditProducerTask = new ProducerTask(TransactionType.CREDIT);
        creditExecutor = Executors.newSingleThreadScheduledExecutor();
        creditExecutor.scheduleAtFixedRate(creditProducerTask, 0, period, TimeUnit.MILLISECONDS);

        ProducerTask debitProducerTask = new ProducerTask(TransactionType.DEBIT);
        debitExecutor = Executors.newSingleThreadScheduledExecutor();
        debitExecutor.scheduleAtFixedRate(debitProducerTask, 0, period, TimeUnit.MILLISECONDS);
    }

    class ProducerTask implements Runnable {
        private TransactionType transactionType;

        public ProducerTask(TransactionType transactionType) {
            this.transactionType = transactionType;
        }

        @Override
        public void run() {
            try {
                Long uniqueID = new Random().nextLong();
                Double amount = getRandomAmount(transactionType);
                Transaction transaction = new Transaction(uniqueID, amount);
                log.info("Generated transaction event with Thread : {}, {} ", Thread.currentThread().getName(), amount);
                bankAccountService.processTransaction(transaction);
            } catch (Exception e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public Double getRandomAmount(TransactionType type) {
        Random r = new Random();
        Double amount = MIN_TRX_AMOUNT + (MAX_TRX_AMOUNT - MIN_TRX_AMOUNT) * r.nextDouble();
        if(type.equals(TransactionType.DEBIT)) {
            return -amount;
        }
        return amount;
    }

    @PreDestroy
    public void shutdownExecutors() {
        try {
            creditExecutor.shutdown();
            debitExecutor.shutdown();
            if (!creditExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
                creditExecutor.shutdownNow();
            }
            if (!debitExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
                debitExecutor.shutdownNow();
            }
            log.info("Executors shut down complete.");
        } catch (InterruptedException e) {
            log.error("Exception while shutting down executors", e);
            creditExecutor.shutdownNow();
            debitExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
