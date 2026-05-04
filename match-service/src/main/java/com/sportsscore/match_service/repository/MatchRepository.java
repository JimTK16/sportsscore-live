package com.sportsscore.match_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sportsscore.match_service.model.Match;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

}
