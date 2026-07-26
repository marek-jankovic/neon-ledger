package com.bank.core_banking.exception;

public class ProtocolViolationException extends RuntimeException {
    public ProtocolViolationException(String message) {
        super(message);
    }
}
