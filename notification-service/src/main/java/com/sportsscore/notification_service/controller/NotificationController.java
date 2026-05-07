package com.sportsscore.notification_service.controller;

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
        return scoreConsumer.getScoreStream()
                .map(event -> ServerSentEvent.<ScoreUpdateEvent>builder().data(event).build());
    }

}
