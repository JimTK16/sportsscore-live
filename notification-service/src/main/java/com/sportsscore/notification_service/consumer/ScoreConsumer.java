package com.sportsscore.notification_service.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.sportsscore.notification_service.model.ScoreUpdateEvent;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
public class ScoreConsumer {
    // A reactive sink that broadcasts messages to multiple subscribers
    private final Sinks.Many<ScoreUpdateEvent> sink = Sinks.many().multicast().onBackpressureBuffer();

    @KafkaListener(topics = "live-scores", groupId = "notification-group")
    public void consume(ScoreUpdateEvent event) {
        System.out.println("Notification Service received: " + event);
        // Push the event into the sink
        sink.tryEmitNext(event);
    }

    // The controller will call this to get the stream of data
    public Flux<ScoreUpdateEvent> getScoreStream() {
        return sink.asFlux();
    }

}
