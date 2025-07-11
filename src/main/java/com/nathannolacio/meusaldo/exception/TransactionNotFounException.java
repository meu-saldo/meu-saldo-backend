package com.nathannolacio.meusaldo.exception;

public class TransactionNotFounException extends RuntimeException {
    public TransactionNotFounException() {
        super("Transação não encontrada");
    }
}
