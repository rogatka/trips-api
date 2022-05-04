package com.example.trips.infrastructure.rabbitmq;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "rabbit")
@ConstructorBinding
class RabbitProperties {

  private final String exchange;

  private final String enrichmentQueueName;

  private final String deadLetterEnrichmentQueueName;

  RabbitProperties(String exchange, String enrichmentQueueName, String deadLetterEnrichmentQueueName) {
    this.exchange = exchange;
    this.enrichmentQueueName = enrichmentQueueName;
    this.deadLetterEnrichmentQueueName = deadLetterEnrichmentQueueName;
  }

  String getExchange() {
    return exchange;
  }

  String getEnrichmentQueueName() {
    return enrichmentQueueName;
  }

  String getDeadLetterEnrichmentQueueName() {
    return deadLetterEnrichmentQueueName;
  }
}
