package com.nathannolacio.meusaldo.exception;

public class IncomeNotFoundException extends RuntimeException {
    public IncomeNotFoundException() {
        super("Despesa não encontrada");
    }
}
