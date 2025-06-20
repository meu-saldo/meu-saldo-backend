package com.nathannolacio.meusaldo.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException() {
        super("O email já está em uso.");
    }
}
