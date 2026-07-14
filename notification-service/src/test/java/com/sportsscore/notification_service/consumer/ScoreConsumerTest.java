package com.sportsscore.notification_service.consumer;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

import com.sportsscore.notification_service.model.ScoreUpdateEvent;

class ScoreConsumerTest {
    @Test
    void broadcastsConsumedScoreUpdateToSubscribers() {
        ScoreConsumer consumer = new ScoreConsumer();
        AtomicReference<ScoreUpdateEvent> received = new AtomicReference<>();
        consumer.getScoreStream().next().subscribe(received::set);

        ScoreUpdateEvent event = new ScoreUpdateEvent(10L, "Melbourne United", "Sydney Kings", 92, 88, "LIVE");
        consumer.consume(event);

        assertThat(received.get()).isEqualTo(event);
    }
}
