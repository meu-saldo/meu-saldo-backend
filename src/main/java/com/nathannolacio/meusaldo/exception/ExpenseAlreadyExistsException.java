package com.nathannolacio.meusaldo.exception;

public class ExpenseAlreadyExistsException extends RuntimeException {
    public ExpenseAlreadyExistsException() {
        super("Já existe uma despesa com esse nome");
    }
}
