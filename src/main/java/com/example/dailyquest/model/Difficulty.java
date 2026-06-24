package com.example.dailyquest.model;

public enum Difficulty {

    T1("Easy", 5, 2),
    T2("Normal", 10, 5),
    T3("Hard", 20, 10),
    T4("Elite", 40,20),
    BOSS("Boss", 100, 50);

    private final String label;
    private final int baseXp;

    // Coins earned when completing this task
    private final int coinReward;

    Difficulty(String label, int baseXp, int coinReward) {
        this.label = label;
        this.baseXp = baseXp;
        this.coinReward = coinReward;
    }

    public String getLabel() {
        return label;
    }

    public int getBaseXp() {
        return baseXp;
    }

    public int getCoinReward() {
        return coinReward;
    }
}
