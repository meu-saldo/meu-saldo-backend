package com.nathannolacio.meusaldo.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("O usuário não foi encontrado.");
    }
}
