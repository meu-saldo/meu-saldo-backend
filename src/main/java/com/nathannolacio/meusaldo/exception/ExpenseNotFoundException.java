package com.nathannolacio.meusaldo.exception;

public class ExpenseNotFoundException extends RuntimeException {
    public ExpenseNotFoundException() {
        super("Despesa não encontrada");
    }
}
