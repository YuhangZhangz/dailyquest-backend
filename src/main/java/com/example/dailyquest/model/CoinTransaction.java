package com.example.dailyquest.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "coin_transactions")
public class CoinTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(nullable = false)
    private int amount;

    @Column(nullable = false)
    private int balanceAfter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CoinTransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CoinTransactionSourceType sourceType;

    private Long sourceId;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public CoinTransaction() {
        // Default constructor for JPA
    }

    public CoinTransaction(AppUser user,
                           int amount,
                           int balanceAfter,
                           CoinTransactionType type,
                           CoinTransactionSourceType sourceType,
                           Long sourceId,
                           String description) {
        this.user = user;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.type = type;
        this.sourceType = sourceType;
        this.sourceId = sourceId;
        this.description = description;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public AppUser getUser() {
        return user;
    }

    public int getAmount() {
        return amount;
    }

    public int getBalanceAfter() {
        return balanceAfter;
    }

    public CoinTransactionType getType() {
        return type;
    }

    public CoinTransactionSourceType getSourceType() {
        return sourceType;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setBalanceAfter(int balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public void setType(CoinTransactionType type) {
        this.type = type;
    }

    public void setSourceType(CoinTransactionSourceType sourceType) {
        this.sourceType = sourceType;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
