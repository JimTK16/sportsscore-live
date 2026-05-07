package com.sportsscore.match_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sportsscore.match_service.model.Match;
import com.sportsscore.match_service.service.MatchService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @PostMapping()
    public ResponseEntity<Match> createMatch(@RequestBody Match match) {

        return ResponseEntity.ok(matchService.createMatch(match));
    }

    @GetMapping()
    public ResponseEntity<List<Match>> getAllMatches() {
        return ResponseEntity.ok(matchService.getAllMatches());
    }

    @PutMapping()
    public ResponseEntity<Match> updateScore(@PathVariable Long id, @RequestParam int homeScore,
            @RequestParam int awayScore) {

        return ResponseEntity.ok(matchService.updateScore(id, homeScore, awayScore));
    }

}
