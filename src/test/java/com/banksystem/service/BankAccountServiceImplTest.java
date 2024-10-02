package com.banksystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.Random;

import com.banksystem.audit.AuditSystem;
import com.banksystem.model.Submission;
import com.banksystem.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BankAccountServiceImplTest {

    @Mock
    AuditSystem auditSystem;

    @InjectMocks
    BankAccountServiceImpl service;

    @Captor
    ArgumentCaptor<Submission> submissionCaptor;

    Random random;

    @BeforeEach
    public void init(){
        service = new BankAccountServiceImpl(auditSystem);
    }
    @Test
    public void whenCreditTransactionIsProcessedThenRetrieveBalanceReturnsSameAmount() {

        Transaction transaction = new Transaction(random.nextLong(),50.0);
        service.processTransaction(transaction);
        assertEquals(50, service.retrieveBalance());

        verify(auditSystem, times(0)).submit(any());

    }

    @Test
    public void whenAccountBalanceIsInsufficientThenDebitTransactionIsIgnored() {
        Transaction transaction = new Transaction(random.nextLong(),-160.0);
        service.processTransaction(transaction);
        assertEquals(0, service.retrieveBalance());

        verify(auditSystem, times(0)).submit(any());
    }

    @Test
    public void whenTotalNumberOfTransactionsExceedThresholdAuditSystemIsInvoked() {
        Transaction transaction = new Transaction(random.nextLong(),1.0);
        for (int i = 1; i <= 1000; i++) {
            service.processTransaction(transaction);
        }
        assertEquals(1000, service.retrieveBalance());
        Mockito.verify(auditSystem, Mockito.times(1)).submit(submissionCaptor.capture());
        Submission submission = submissionCaptor.getValue();
        assertEquals(1, submission.getBatches().size());
        assertEquals(1000, submission.getBatches().peek().getTotalValueOfAllTransactions());
    }
}