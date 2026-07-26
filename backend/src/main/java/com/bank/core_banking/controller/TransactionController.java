package com.bank.core_banking.controller;

import com.bank.core_banking.dto.TransferRequest;
import com.bank.core_banking.model.Transaction;
import com.bank.core_banking.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Bank Engine", description = "Operations for District 01 Ledger")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transfer")
    @Operation(summary = "Execute fund transfer", description = "Moves credits between two demo Slovak accounts")
    public ResponseEntity<String> transfer(@Valid @RequestBody TransferRequest request) {
        transactionService.transferMoney(request.from(), request.to(), request.amount());
        return ResponseEntity.ok("Transfer completed.");
    }

    @GetMapping("/ledger")
    @Operation(summary = "Get transaction history")
    public List<Transaction> getHistory() {
        return transactionService.getAllTransactions();
    }

    @GetMapping("/balance/{iban}")
    @Operation(summary = "Check account balance")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable String iban) {
        return ResponseEntity.ok(transactionService.getAccountBalance(iban));
    }
}
