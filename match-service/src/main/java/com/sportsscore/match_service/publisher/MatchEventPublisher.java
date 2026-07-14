package com.sportsscore.match_service.publisher;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sportsscore.match_service.model.ScoreUpdateEvent;

@Component
public class MatchEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(MatchEventPublisher.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "live-scores";

    public MatchEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendScoreUpdate(ScoreUpdateEvent event) {
        // Sends the event to Kafka, using the matchId as the routing key
        kafkaTemplate.send(TOPIC, event.getMatchId().toString(), event)
                .whenComplete((result, error) -> {
                    if (error != null) {
                        log.error("Unable to publish score update for matchId={}", event.getMatchId(), error);
                    } else {
                        log.info("Published score update for matchId={} to topic={}", event.getMatchId(), TOPIC);
                    }
                });
    }
}
