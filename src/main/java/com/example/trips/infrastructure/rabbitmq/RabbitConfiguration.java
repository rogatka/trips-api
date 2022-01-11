package com.example.trips.infrastructure.rabbitmq;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;

@Configuration
class RabbitConfiguration implements RabbitListenerConfigurer {

  public static final String TRIPS_TOPIC = "trips";

  public static final String TRIPS_ENRICHMENT_QUEUE = "enrichment-queue";

  public static final String TRIPS_ENRICHMENT_BINDING_KEY = "trips.enrichment";

  @Bean
  public AmqpAdmin amqpAdmin(final ConnectionFactory connectionFactory) {
    return new RabbitAdmin(connectionFactory);
  }

  @Bean
  public Declarables exchangeBindings(RabbitProperties rabbitProperties) {
    TopicExchange tripsTopicExchange = new TopicExchange(rabbitProperties.getExchange());
    Queue enrichmentQueue = QueueBuilder.durable(
        rabbitProperties.getEnrichmentQueueName()).build();

    return new Declarables(
        enrichmentQueue,
        tripsTopicExchange,
        BindingBuilder
            .bind(enrichmentQueue)
            .to(tripsTopicExchange)
            .with(rabbitProperties.getEnrichmentQueueBindingKey()));
  }

  @Override
  public void configureRabbitListeners(RabbitListenerEndpointRegistrar registrar) {
    registrar.setMessageHandlerMethodFactory(messageHandlerMethodFactory());
  }

  @Bean
  public MessageHandlerMethodFactory messageHandlerMethodFactory() {
    DefaultMessageHandlerMethodFactory messageHandlerMethodFactory
        = new DefaultMessageHandlerMethodFactory();
    messageHandlerMethodFactory.setMessageConverter(consumerJackson2MessageConverter());
    return messageHandlerMethodFactory;
  }

  @Bean
  public MappingJackson2MessageConverter consumerJackson2MessageConverter() {
    return new MappingJackson2MessageConverter();
  }

  @Bean
  public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory,
      RabbitProperties rabbitProperties) {
    final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
    rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
    rabbitTemplate.setExchange(rabbitProperties.getExchange());
    rabbitTemplate.setRoutingKey(rabbitProperties.getEnrichmentQueueBindingKey());
    return rabbitTemplate;
  }

  @Bean
  public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
    return new Jackson2JsonMessageConverter();
  }
}
