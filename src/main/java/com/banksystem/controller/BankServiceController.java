package com.banksystem.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banksystem.service.BankAccountService;

@RestController
public class BankServiceController {

    private BankAccountService bankAccountService;

    public BankServiceController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @GetMapping("/bankservice/balance")
    public ResponseEntity<Double> getBalance(){
        Double balance = null;
        try {
            balance = bankAccountService.retrieveBalance();
            return ResponseEntity.ok(balance);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(balance);
        }
    }
}

