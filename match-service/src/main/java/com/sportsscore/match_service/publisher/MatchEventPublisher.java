package com.sportsscore.match_service.publisher;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.sportsscore.match_service.model.ScoreUpdateEvent;

@Component
public class MatchEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "live-scores";

    public MatchEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendScoreUpdate(ScoreUpdateEvent event) {
        // Sends the event to Kafka, using the matchId as the routing key
        kafkaTemplate.send(TOPIC, event.getMatchId().toString(), event);
        System.out.println("Published score update to Kafka: " + event);
    }
}
