package com.sportsscore.notification_service.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sportsscore.notification_service.model.ScoreUpdateEvent;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
public class ScoreConsumer {
    private static final Logger log = LoggerFactory.getLogger(ScoreConsumer.class);
    // A reactive sink that broadcasts messages to multiple subscribers
    private final Sinks.Many<ScoreUpdateEvent> sink = Sinks.many().multicast().onBackpressureBuffer();

    @KafkaListener(topics = "live-scores", groupId = "notification-group")
    public void consume(ScoreUpdateEvent event) {
        Sinks.EmitResult result = sink.tryEmitNext(event);
        if (result.isFailure()) {
            log.warn("Score update for matchId={} was not delivered to the live stream: {}", event.getMatchId(), result);
        } else {
            log.info("Broadcast score update for matchId={}", event.getMatchId());
        }
    }

    // The controller will call this to get the stream of data
    public Flux<ScoreUpdateEvent> getScoreStream() {
        return sink.asFlux();
    }

}
