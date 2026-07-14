package com.sportsscore.match_service.controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record ScoreUpdateRequest(
        @Min(0) @Max(999) int homeScore,
        @Min(0) @Max(999) int awayScore) {
}
