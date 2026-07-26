package com.bank.core_banking.service;

import com.bank.core_banking.model.Account;
import com.bank.core_banking.exception.InsufficientFundsException;
import com.bank.core_banking.repository.AccountRepository;
import com.bank.core_banking.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class TransactionIntegrationTest {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    void shouldPerformAtomicDatabaseTransfer() {
        // GIVEN
        accountRepository.save(new Account("SK_SENDER", new BigDecimal("1000.00")));
        accountRepository.save(new Account("SK_RECEIVER", new BigDecimal("500.00")));

        // WHEN
        transactionService.transferMoney("SK_SENDER", "SK_RECEIVER", new BigDecimal("200.00"));

        // THEN
        Account sender = accountRepository.findByIban("SK_SENDER").orElseThrow();
        Account receiver = accountRepository.findByIban("SK_RECEIVER").orElseThrow();

        assertThat(sender.getBalance()).isEqualByComparingTo("800.00");
        assertThat(receiver.getBalance()).isEqualByComparingTo("700.00");
        assertThat(transactionRepository.findAll()).hasSize(1);
    }

    @Test
    void shouldRollBackWhenFundsAreInsufficient() {
        accountRepository.save(new Account("SK_ROLLBACK_FROM", new BigDecimal("10.00")));
        accountRepository.save(new Account("SK_ROLLBACK_TO", new BigDecimal("20.00")));

        assertThatThrownBy(() ->
                transactionService.transferMoney(
                        "SK_ROLLBACK_FROM",
                        "SK_ROLLBACK_TO",
                        new BigDecimal("50.00")
                ))
                .isInstanceOf(InsufficientFundsException.class);

        assertThat(accountRepository.findByIban("SK_ROLLBACK_FROM").orElseThrow().getBalance())
                .isEqualByComparingTo("10.00");
        assertThat(accountRepository.findByIban("SK_ROLLBACK_TO").orElseThrow().getBalance())
                .isEqualByComparingTo("20.00");
        assertThat(transactionRepository.findAll()).isEmpty();
    }
}
