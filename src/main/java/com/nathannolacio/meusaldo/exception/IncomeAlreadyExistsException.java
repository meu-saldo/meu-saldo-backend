package com.nathannolacio.meusaldo.exception;

public class IncomeAlreadyExistsException extends RuntimeException {
    public IncomeAlreadyExistsException() {
        super("Já existe uma entrada com esse descrição");
    }
}
