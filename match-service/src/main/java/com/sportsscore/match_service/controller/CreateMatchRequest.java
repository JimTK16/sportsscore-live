package com.sportsscore.match_service.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateMatchRequest(
        @NotBlank @Size(max = 80) String homeTeam,
        @NotBlank @Size(max = 80) String awayTeam) {
}
