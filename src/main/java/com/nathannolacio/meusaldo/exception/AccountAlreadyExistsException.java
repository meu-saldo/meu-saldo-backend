package com.nathannolacio.meusaldo.exception;

public class AccountAlreadyExistsException extends RuntimeException {
    public AccountAlreadyExistsException() {
        super("Já existe uma conta cadastrada com esse nome");
    }
}
