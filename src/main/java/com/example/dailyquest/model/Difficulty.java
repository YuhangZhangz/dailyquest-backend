package com.example.dailyquest.model;

public enum Difficulty {

    T1("Easy", 5),
    T2("Normal", 10),
    T3("Hard", 20),
    T4("Elite", 40),
    BOSS("Boss", 100);

    private final String label;
    private final int baseXp;

    Difficulty(String label, int baseXp) {
        this.label = label;
        this.baseXp = baseXp;
    }

    public String getLabel() {
        return label;
    }

    public int getBaseXp() {
        return baseXp;
    }
}
