package com.example.dailyquest.service;

import com.example.dailyquest.dto.request.CreateRewardRequest;
import com.example.dailyquest.dto.request.UpdateRewardRequest;
import com.example.dailyquest.dto.response.RewardResponse;
import com.example.dailyquest.dto.response.RewardSummaryResponse;
import com.example.dailyquest.dto.response.RedeemRewardResponse;
import com.example.dailyquest.exception.InvalidCredentialsException;
import com.example.dailyquest.exception.RewardNotFoundException;
import com.example.dailyquest.model.AppUser;
import com.example.dailyquest.model.CoinTransaction;
import com.example.dailyquest.model.CoinTransactionSourceType;
import com.example.dailyquest.model.CoinTransactionType;
import com.example.dailyquest.model.Reward;
import com.example.dailyquest.model.RewardRedemption;
import com.example.dailyquest.repository.AppUserRepository;
import com.example.dailyquest.repository.CoinTransactionRepository;
import com.example.dailyquest.repository.RewardRedemptionRepository;
import com.example.dailyquest.repository.RewardRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RewardService {

    private final RewardRepository rewardRepository;
    private final RewardRedemptionRepository rewardRedemptionRepository;
    private final CoinTransactionRepository coinTransactionRepository;
    private final AppUserRepository appUserRepository;

    public RewardService(
            RewardRepository rewardRepository,
            RewardRedemptionRepository rewardRedemptionRepository,
            CoinTransactionRepository coinTransactionRepository,
            AppUserRepository appUserRepository) {
        this.rewardRepository = rewardRepository;
        this.rewardRedemptionRepository = rewardRedemptionRepository;
        this.coinTransactionRepository = coinTransactionRepository;
        this.appUserRepository = appUserRepository;
    }

    public List<RewardResponse> getActiveRewardsForCurrentUser() {
        AppUser currentUser = getCurrentUser();
        return rewardRepository.findByUserIdAndActiveTrue(currentUser.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public RewardResponse createReward(CreateRewardRequest request) {
        AppUser currentUser = getCurrentUser();
        Reward reward = new Reward(
                request.title(),
                request.description(),
                request.cost(),
                request.iconKey()
        );
        reward.setUser(currentUser);

        Reward savedReward = rewardRepository.save(reward);
        return toResponse(savedReward);
    }

    public RewardResponse updateReward(Long rewardId, UpdateRewardRequest request) {
        AppUser currentUser = getCurrentUser();
        Reward reward = rewardRepository.findByIdAndUserId(rewardId, currentUser.getId())
                .orElseThrow(() -> new RewardNotFoundException(rewardId));

        reward.setTitle(request.title());
        reward.setDescription(request.description());
        reward.setCost(request.cost());
        reward.setIconKey(request.iconKey());

        Reward updatedReward = rewardRepository.save(reward);
        return toResponse(updatedReward);
    }

    public void softDeleteReward(Long rewardId) {
        AppUser currentUser = getCurrentUser();
        Reward reward = rewardRepository.findByIdAndUserId(rewardId, currentUser.getId())
                .orElseThrow(() -> new RewardNotFoundException(rewardId));

        reward.setActive(false);
        rewardRepository.save(reward);
    }

    @Transactional
    public RedeemRewardResponse redeemReward(Long rewardId) {
        AppUser currentUser = getCurrentUser();
        Reward reward = rewardRepository.findByIdAndUserId(rewardId, currentUser.getId())
                .orElseThrow(() -> new RewardNotFoundException(rewardId));

        if (!reward.getActive()) {
            throw new IllegalStateException("Reward is not active");
        }

        if (currentUser.getCoinBalance() < reward.getCost()) {
            throw new IllegalStateException("Insufficient coin balance");
        }

        int newBalance = currentUser.getCoinBalance() - reward.getCost();
        currentUser.setCoinBalance(newBalance);
        currentUser = appUserRepository.save(currentUser);

        RewardRedemption redemption = new RewardRedemption(reward, currentUser);
        rewardRedemptionRepository.save(redemption);

        coinTransactionRepository.save(new CoinTransaction(
                currentUser,
                -reward.getCost(),
                newBalance,
                CoinTransactionType.REWARD_REDEEM,
                CoinTransactionSourceType.REWARD,
                reward.getId(),
                "Redeemed reward: " + reward.getTitle()
        ));

        return new RedeemRewardResponse(reward.getId(), reward.getCost(), newBalance);
    }

    public RewardSummaryResponse getRewardSummary() {
        AppUser currentUser = getCurrentUser();
        long redeemedCount = rewardRedemptionRepository.countByUserId(currentUser.getId());

        return new RewardSummaryResponse(
                currentUser.getCoinBalance(),
                redeemedCount
        );
    }

    private RewardResponse toResponse(Reward reward) {
        return new RewardResponse(
                reward.getId(),
                reward.getTitle(),
                reward.getDescription(),
                reward.getCost(),
                reward.getIconKey(),
                reward.getActive(),
                reward.getCreatedAt()
        );
    }

    private AppUser getCurrentUser() {
        Object principal = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if (!(principal instanceof AppUser user)) {
            throw new InvalidCredentialsException();
        }

        return user;
    }
}
