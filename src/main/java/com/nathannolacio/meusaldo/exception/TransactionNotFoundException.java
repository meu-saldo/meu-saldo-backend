package com.nathannolacio.meusaldo.exception;

public class TransactionNotFoundException extends RuntimeException {
    public TransactionNotFoundException() {
        super("Transação não encontrada");
    }
}
