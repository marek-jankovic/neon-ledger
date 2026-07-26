package com.bank.core_banking.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransferRequest(
        @NotBlank(message = "Sender account is required")
        String from,

        @NotBlank(message = "Receiver account is required")
        String to,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be at least 0.01")
        @Digits(integer = 17, fraction = 2, message = "Amount may have at most two decimal places")
        BigDecimal amount
) {
}
