package com.sportsscore.match_service;

import com.sportsscore.match_service.model.Match;
import com.sportsscore.match_service.model.ScoreUpdateEvent;
import com.sportsscore.match_service.service.MatchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.kafka.consumer.auto-offset-reset=earliest",
        "spring.kafka.consumer.group-id=test-group",
        "spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer",
        "spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JsonDeserializer",
        "spring.kafka.consumer.properties.spring.json.trusted.packages=*",
        "spring.cache.type=none" // Protects against the Redis crash
})
@Testcontainers
@Import(MatchKafkaIntegrationTest.KafkaTestConfig.class)
class MatchKafkaIntegrationTest {

    // 1. Spin up the Database (Magic Annotation works fine here)
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    // 2. Spin up Kafka (Removed ServiceConnection)
    @Container
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"));

    // --- The Manual Wiring for Kafka ---
    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        // Explicitly tells Spring exactly what random port Docker assigned to Kafka
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }
    // -----------------------------------

    @Autowired
    MatchService matchService;

    @Autowired
    DummyConsumer dummyConsumer;

    @TestConfiguration
    static class KafkaTestConfig {
        @Bean
        public DummyConsumer dummyConsumer() {
            return new DummyConsumer();
        }
    }

    public static class DummyConsumer {
        private final BlockingQueue<ScoreUpdateEvent> events = new LinkedBlockingQueue<>();

        @KafkaListener(topics = "live-scores", groupId = "test-group")
        public void consume(ScoreUpdateEvent event) {
            events.add(event);
        }

        public ScoreUpdateEvent getLatestEvent() throws InterruptedException {
            return events.poll(10, TimeUnit.SECONDS);
        }
    }

    @Test
    void whenScoreIsUpdated_thenEventIsPublishedToKafka() throws InterruptedException {
        // Arrange
        Match savedMatch = matchService.createMatch("Melbourne United", "Sydney Kings");

        // Act
        matchService.updateScore(savedMatch.getId(), 105, 98);

        // Assert
        ScoreUpdateEvent receivedEvent = dummyConsumer.getLatestEvent();

        assertThat(receivedEvent).isNotNull();
        assertThat(receivedEvent.getMatchId()).isEqualTo(savedMatch.getId());
        assertThat(receivedEvent.getHomeScore()).isEqualTo(105);
        assertThat(receivedEvent.getAwayScore()).isEqualTo(98);
        assertThat(receivedEvent.getStatus()).isEqualTo("LIVE");

        System.out.println("Kafka Test Passed! Intercepted event: " + receivedEvent);
    }
}
