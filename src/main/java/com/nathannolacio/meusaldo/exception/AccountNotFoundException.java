package com.nathannolacio.meusaldo.exception;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException() {
        super("Conta não encontrada.");
    }
}
