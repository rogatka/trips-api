package com.example.trips.infrastructure.rabbitmq;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "rabbit")
class RabbitProperties {
    private Trip trip;

    static class Trip {
        private String exchange;
        private String startQueueName;
        private String finishQueueName;
        private String startQueueBindingKey;
        private String finishQueueBindingKey;

        public String getExchange() {
            return exchange;
        }

        public void setExchange(String exchange) {
            this.exchange = exchange;
        }

        public String getStartQueueName() {
            return startQueueName;
        }

        public void setStartQueueName(String startQueueName) {
            this.startQueueName = startQueueName;
        }

        public String getFinishQueueName() {
            return finishQueueName;
        }

        public void setFinishQueueName(String finishQueueName) {
            this.finishQueueName = finishQueueName;
        }

        public String getStartQueueBindingKey() {
            return startQueueBindingKey;
        }

        public void setStartQueueBindingKey(String startQueueBindingKey) {
            this.startQueueBindingKey = startQueueBindingKey;
        }

        public String getFinishQueueBindingKey() {
            return finishQueueBindingKey;
        }

        public void setFinishQueueBindingKey(String finishQueueBindingKey) {
            this.finishQueueBindingKey = finishQueueBindingKey;
        }
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }
}
