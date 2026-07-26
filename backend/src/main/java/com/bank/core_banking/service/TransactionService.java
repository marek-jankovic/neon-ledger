package com.bank.core_banking.service;

import com.bank.core_banking.exception.InsufficientFundsException;
import com.bank.core_banking.exception.AccountNotFoundException;
import com.bank.core_banking.exception.ProtocolViolationException;
import com.bank.core_banking.model.Account;
import com.bank.core_banking.model.Transaction;
import com.bank.core_banking.model.TransactionStatus;
import com.bank.core_banking.repository.AccountRepository;
import com.bank.core_banking.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

@Service
public class TransactionService {
    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransactionService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public void transferMoney(String fromIban, String toIban, BigDecimal amount) {
        fromIban = normalizeIban(fromIban);
        toIban = normalizeIban(toIban);
        log.info("Processing transfer from {} to {} | Amount: {}", fromIban, toIban, amount);

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ProtocolViolationException("Amount must be greater than zero");
        }
        if (amount.scale() > 2) {
            throw new ProtocolViolationException("Amount may have at most two decimal places");
        }
        if (!isDemoSlovakAccount(fromIban) || !isDemoSlovakAccount(toIban)) {
            throw new ProtocolViolationException("Both account identifiers must start with SK");
        }
        if (fromIban.equals(toIban)) {
            throw new ProtocolViolationException("Sender and receiver accounts must be different");
        }

        Account sender = accountRepository.findByIban(fromIban)
                .orElseThrow(() -> new AccountNotFoundException("Sender account not found"));
        Account receiver = accountRepository.findByIban(toIban)
                .orElseThrow(() -> new AccountNotFoundException("Receiver account not found"));

        if (sender.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds. Required: " + amount);
        }

        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        accountRepository.save(sender);
        accountRepository.save(receiver);

        Transaction logEntry = new Transaction();
        logEntry.setSenderIban(fromIban);
        logEntry.setReceiverIban(toIban);
        logEntry.setAmount(amount);
        logEntry.setStatus(TransactionStatus.SUCCESS);
        transactionRepository.save(logEntry);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public BigDecimal getAccountBalance(String iban) {
        return accountRepository.findByIban(normalizeIban(iban))
                .map(Account::getBalance)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
    }

    private String normalizeIban(String iban) {
        if (iban == null) {
            return "";
        }
        return iban.replaceAll("\\s+", "").toUpperCase(Locale.ROOT);
    }

    private boolean isDemoSlovakAccount(String iban) {
        return iban.startsWith("SK") && iban.length() >= 6 && iban.length() <= 34;
    }
}
