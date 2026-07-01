package com.sportsscore.match_service;

import com.sportsscore.match_service.model.Match;
import com.sportsscore.match_service.repository.MatchRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class MatchRepositoryIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @Autowired
    MatchRepository matchRepository;

    @Test
    void connectionEstablishedAndMatchCanBeSaved() {
        // Arrange
        Match match = new Match();
        match.setHomeTeam("Melbourne United");
        match.setAwayTeam("Sydney Kings");
        match.setStatus(Match.MatchStatus.SCHEDULED);

        // Act
        Match savedMatch = matchRepository.save(match);

        // Assert
        assertThat(savedMatch.getId()).isNotNull();
        assertThat(matchRepository.findAll()).hasSize(1);

        System.out.println("Integration Test Passed! Saved Match ID: " + savedMatch.getId());
    }
}
