package com.sportsscore.notification_service.controller;

import java.time.Duration;

import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sportsscore.notification_service.consumer.ScoreConsumer;
import com.sportsscore.notification_service.model.ScoreUpdateEvent;

import reactor.core.publisher.Flux;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {
    private final ScoreConsumer scoreConsumer;

    public NotificationController(ScoreConsumer scoreConsumer) {
        this.scoreConsumer = scoreConsumer;
    }

    // Produces TEXT_EVENT_STREAM_VALUE which tells the browser to keep the
    // connection open
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<ScoreUpdateEvent>> streamScores() {
        // 1. Our actual score data stream
        Flux<ServerSentEvent<ScoreUpdateEvent>> dataStream = scoreConsumer.getScoreStream()
                .map(event -> ServerSentEvent.<ScoreUpdateEvent>builder().data(event).build());

        // 2. A heartbeat stream (sends an empty comment every 15 seconds)
        Flux<ServerSentEvent<ScoreUpdateEvent>> keepAliveStream = Flux.interval(Duration.ofSeconds(15))
                .map(tick -> ServerSentEvent.<ScoreUpdateEvent>builder().comment("keep-alive").build());

        // Merge them together so the connection never idles and dies
        return Flux.merge(dataStream, keepAliveStream);
    }

}
