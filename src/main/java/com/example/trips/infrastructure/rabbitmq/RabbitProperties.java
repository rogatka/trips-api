package com.example.trips.infrastructure.rabbitmq;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "rabbit")
@ConstructorBinding
class RabbitProperties {

  private final String exchange;

  private final String enrichmentQueueName;

  private final String enrichmentQueueBindingKey;

  RabbitProperties(String exchange, String enrichmentQueueName, String enrichmentQueueBindingKey) {
    this.exchange = exchange;
    this.enrichmentQueueName = enrichmentQueueName;
    this.enrichmentQueueBindingKey = enrichmentQueueBindingKey;
  }

  String getExchange() {
    return exchange;
  }

  String getEnrichmentQueueName() {
    return enrichmentQueueName;
  }

  String getEnrichmentQueueBindingKey() {
    return enrichmentQueueBindingKey;
  }
}
