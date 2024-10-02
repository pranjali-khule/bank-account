package com.banksystem.service;

import com.banksystem.model.Transaction;

public interface BankAccountService {
    public void processTransaction(Transaction transaction);
    public double retrieveBalance();
}
