package com.sportsscore.match_service.service;

public class MatchNotFoundException extends RuntimeException {
    public MatchNotFoundException(Long matchId) {
        super("Match " + matchId + " was not found");
    }
}
