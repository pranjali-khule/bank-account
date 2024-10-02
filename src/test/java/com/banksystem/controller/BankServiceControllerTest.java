package com.banksystem.controller;


import java.util.Random;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.banksystem.model.Transaction;
import com.banksystem.service.BankAccountService;


@WebMvcTest(BankServiceController.class)
public class BankServiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BankAccountService bankAccountService;

    @Test
    public void whenTransactionProcessedThenRetrieveBalanceReturnsBalance() throws Exception {
        double balance = 150.75;
        Transaction transaction = new Transaction(new Random().nextLong(), balance);
        Mockito.doAnswer(invocation -> {
            Transaction amount = invocation.getArgument(0, Transaction.class);
            Mockito.when(bankAccountService.retrieveBalance()).thenReturn(amount.getAmount());
            return null;
        }).when(bankAccountService).processTransaction(transaction);

        bankAccountService.processTransaction(transaction);

        mockMvc.perform(MockMvcRequestBuilders.get("/bankservice/balance")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(Double.toString(balance)));
    }
}