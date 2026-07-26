package com.bank.core_banking.controller;

import com.bank.core_banking.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
@Import(com.bank.core_banking.config.SecurityConfig.class)
class TransactionControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Test
    void shouldRejectUnauthenticatedRequests() throws Exception {
        mockMvc.perform(get("/api/transactions/ledger"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAllowConfiguredDemoUser() throws Exception {
        mockMvc.perform(get("/api/transactions/ledger")
                        .with(httpBasic("admin", "change-me-locally")))
                .andExpect(status().isOk());
    }
}
