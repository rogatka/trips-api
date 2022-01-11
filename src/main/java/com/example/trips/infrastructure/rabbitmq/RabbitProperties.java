package com.example.trips.infrastructure.rabbitmq;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "rabbit")
@ConstructorBinding
class RabbitProperties {

  private final String topic;

  private final String exchange;

  private final String enrichmentQueueName;

  private final String enrichmentQueueBindingKey;

  public RabbitProperties(String topic, String exchange, String enrichmentQueueName,
      String enrichmentQueueBindingKey) {
    this.topic = topic;
    this.exchange = exchange;
    this.enrichmentQueueName = enrichmentQueueName;
    this.enrichmentQueueBindingKey = enrichmentQueueBindingKey;
  }

  public String getTopic() {
    return topic;
  }

  public String getExchange() {
    return exchange;
  }

  public String getEnrichmentQueueName() {
    return enrichmentQueueName;
  }

  public String getEnrichmentQueueBindingKey() {
    return enrichmentQueueBindingKey;
  }
}
