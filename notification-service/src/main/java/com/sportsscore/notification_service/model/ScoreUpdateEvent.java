package com.sportsscore.notification_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScoreUpdateEvent {
    private Long matchId;
    private String homeTeam;
    private String awayTeam;
    private int homeScore;
    private int awayScore;
    private String status;

}
