package com.sportsscore.match_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sportsscore.match_service.model.Match;

public interface MatchRepository extends JpaRepository<Match, Long> {

}
