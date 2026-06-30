package com.example.dailyquest.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum GrowthCategory {
    NONE,
    WORK,
    SCHOOL,
    HEALTH,
    PERSONAL;

    @JsonCreator
    public static GrowthCategory fromValue(String value) {
        if (value == null || value.isBlank()) {
            return NONE;
        }

        return GrowthCategory.valueOf(value);
    }
}
