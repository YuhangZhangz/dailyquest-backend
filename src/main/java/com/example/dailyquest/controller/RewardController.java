package com.example.dailyquest.controller;

import com.example.dailyquest.dto.request.CreateRewardRequest;
import com.example.dailyquest.dto.request.UpdateRewardRequest;
import com.example.dailyquest.dto.response.RewardResponse;
import com.example.dailyquest.dto.response.RewardSummaryResponse;
import com.example.dailyquest.dto.response.RedeemRewardResponse;
import com.example.dailyquest.service.RewardService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rewards")
public class RewardController {

    private final RewardService rewardService;

    public RewardController(RewardService rewardService) {
        this.rewardService = rewardService;
    }

    @GetMapping
    public List<RewardResponse> getRewards() {
        return rewardService.getActiveRewardsForCurrentUser();
    }

    @PostMapping
    public RewardResponse createReward(@Valid @RequestBody CreateRewardRequest request) {
        return rewardService.createReward(request);
    }

    @PutMapping("/{rewardId}")
    public RewardResponse updateReward(
            @PathVariable Long rewardId,
            @Valid @RequestBody UpdateRewardRequest request
    ) {
        return rewardService.updateReward(rewardId, request);
    }

    @DeleteMapping("/{rewardId}")
    public void deleteReward(@PathVariable Long rewardId) {
        rewardService.softDeleteReward(rewardId);
    }

    @PostMapping("/{rewardId}/redeem")
    public RedeemRewardResponse redeemReward(@PathVariable Long rewardId) {
        return rewardService.redeemReward(rewardId);
    }

    @GetMapping("/summary")
    public RewardSummaryResponse getSummary() {
        return rewardService.getRewardSummary();
    }
}
