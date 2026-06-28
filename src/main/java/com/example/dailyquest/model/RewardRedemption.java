package com.example.dailyquest.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reward_redemptions")
public class RewardRedemption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reward_id", nullable = false)
    private Reward reward;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(nullable = false)
    private String rewardTitleSnapshot;

    @Column(nullable = false)
    private String rewardDescriptionSnapshot;

    @Column(nullable = false)
    private int costPaid;

    @Column(nullable = false)
    private LocalDateTime redeemedAt;

    public RewardRedemption() {
        // Default constructor for JPA
    }

    public RewardRedemption(Reward reward, AppUser user) {
        this.reward = reward;
        this.user = user;
        this.rewardTitleSnapshot = reward.getTitle();
        this.rewardDescriptionSnapshot = reward.getDescription();
        this.costPaid = reward.getCost();
        this.redeemedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Reward getReward() {
        return reward;
    }

    public AppUser getUser() {
        return user;
    }

    public String getRewardTitleSnapshot() {
        return rewardTitleSnapshot;
    }

    public String getRewardDescriptionSnapshot() {
        return rewardDescriptionSnapshot;
    }

    public int getCostPaid() {
        return costPaid;
    }

    public LocalDateTime getRedeemedAt() {
        return redeemedAt;
    }

    public void setReward(Reward reward) {
        this.reward = reward;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public void setRewardTitleSnapshot(String rewardTitleSnapshot) {
        this.rewardTitleSnapshot = rewardTitleSnapshot;
    }

    public void setRewardDescriptionSnapshot(String rewardDescriptionSnapshot) {
        this.rewardDescriptionSnapshot = rewardDescriptionSnapshot;
    }

    public void setCostPaid(int costPaid) {
        this.costPaid = costPaid;
    }

    public void setRedeemedAt(LocalDateTime redeemedAt) {
        this.redeemedAt = redeemedAt;
    }
}
