package com.example.trips.infrastructure.rabbitmq;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "rabbit")
class RabbitProperties {
    private Trip trip;

    static class Trip {
        private String topic;
        private String exchange;
        private String enrichmentQueueName;
        private String enrichmentQueueBindingKey;

        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }

        public String getExchange() {
            return exchange;
        }

        public void setExchange(String exchange) {
            this.exchange = exchange;
        }

        public String getEnrichmentQueueName() {
            return enrichmentQueueName;
        }

        public void setEnrichmentQueueName(String enrichmentQueueName) {
            this.enrichmentQueueName = enrichmentQueueName;
        }

        public String getEnrichmentQueueBindingKey() {
            return enrichmentQueueBindingKey;
        }

        public void setEnrichmentQueueBindingKey(String enrichmentQueueBindingKey) {
            this.enrichmentQueueBindingKey = enrichmentQueueBindingKey;
        }
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }
}
