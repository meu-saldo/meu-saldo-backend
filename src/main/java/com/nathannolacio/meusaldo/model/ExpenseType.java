package com.nathannolacio.meusaldo.model;

public enum ExpenseType {
    ESSENTIAL("Essencial"),
    NOT_ESSENTIAL("Não Essencial");

    private final String label;

    ExpenseType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
