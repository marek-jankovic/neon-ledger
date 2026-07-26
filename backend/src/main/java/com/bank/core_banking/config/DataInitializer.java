package com.bank.core_banking.config;

import com.bank.core_banking.model.Account;
import com.bank.core_banking.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

/**
 * Configuration component to seed the in-memory database with initial data.
 * Runs automatically upon application startup.
 */
@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner initDatabase(AccountRepository accountRepository) {
        return args -> {
            log.info("Cortex Node: Seeding initial account data...");

            // Creating a wealthy sender account for District 01 testing
            if (accountRepository.findByIban("SK111222333").isEmpty()) {
                accountRepository.save(new Account("SK111222333", new BigDecimal("5000.00")));
                log.info("Account SK111222333 created with 5000.00 credit.");
            }

            // Creating a standard receiver account
            if (accountRepository.findByIban("SK999888777").isEmpty()) {
                accountRepository.save(new Account("SK999888777", new BigDecimal("100.00")));
                log.info("Account SK999888777 created with 100.00 credit.");
            }

            log.info("Database seeding completed. System ready for transactions.");
        };
    }
}
