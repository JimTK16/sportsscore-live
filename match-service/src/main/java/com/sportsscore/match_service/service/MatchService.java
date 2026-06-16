package com.sportsscore.match_service.service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.sportsscore.match_service.model.Match;
import com.sportsscore.match_service.model.ScoreUpdateEvent;
import com.sportsscore.match_service.publisher.MatchEventPublisher;
import com.sportsscore.match_service.repository.MatchRepository;

import jakarta.transaction.Transactional;

@Service
public class MatchService {
    private final MatchRepository matchRepository;
    private final MatchEventPublisher eventPublisher;

    public MatchService(MatchRepository matchRepository, MatchEventPublisher eventPublisher) {
        this.matchRepository = matchRepository;
        this.eventPublisher = eventPublisher;
    }

    @CacheEvict(value = "matches", allEntries = true)
    public Match createMatch(Match match) {
        match.setStatus(Match.MatchStatus.SCHEDULED);
        return matchRepository.save(match);
    }

    @Cacheable(value = "matches")
    public List<Match> getAllMatches() {
        System.out.println("Fetching matches from Postgres database....");
        return matchRepository.findAll();
    }

    @Transactional
    @CacheEvict(value = "matches", allEntries = true)
    public Match updateScore(Long matchId, int homeScore, int awayScore) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found with id: " + matchId));

        match.setHomeScore(homeScore);
        match.setAwayScore(awayScore);
        match.setStatus(Match.MatchStatus.LIVE);

        // Save to db
        Match savedMatch = matchRepository.save(match);

        // Publish to kafka
        ScoreUpdateEvent event = new ScoreUpdateEvent(
                savedMatch.getId(),
                savedMatch.getHomeTeam(),
                savedMatch.getAwayTeam(),
                savedMatch.getHomeScore(),
                savedMatch.getAwayScore(),
                savedMatch.getStatus().name());
        eventPublisher.sendScoreUpdate(event);

        return savedMatch;
    }
}
