package com.example.trips.infrastructure.rabbitmq.consumer;

import com.example.trips.infrastructure.rabbitmq.model.TripMessageDto;
import com.example.trips.api.service.TripService;
import com.example.trips.api.service.TripTimeEnricher;
import com.example.trips.Trip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

public class RabbitConsumer {
    private final Logger log = LoggerFactory.getLogger(RabbitConsumer.class);

    private final TripService tripService;
    private final TripTimeEnricher tripTimeEnricher;

    public RabbitConsumer(TripService tripService, TripTimeEnricher tripTimeEnricher) {
        this.tripService = tripService;
        this.tripTimeEnricher = tripTimeEnricher;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "start-queue", durable = "true"),
            exchange = @Exchange(value = "trips"), key = "trips"))
    public void enrichStartTime(TripMessageDto messageDto) {
        try {
            Trip trip = tripService.findById(messageDto.getId());
            trip = tripTimeEnricher.enrichStartTime(trip);
            tripService.update(trip);
        } catch (Exception ex) {
            log.error("Error while enriching trip's (id={}) start time. Message: {}", messageDto.getId(), ex.getMessage());
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "finish-queue", durable = "true"),
            exchange = @Exchange(value = "trips"), key = "trips"))
    public void enrichEndTime(TripMessageDto messageDto) {
        try {
            Trip trip = tripService.findById(messageDto.getId());
            trip = tripTimeEnricher.enrichEndTime(trip);
            tripService.update(trip);
        } catch (Exception ex) {
            log.error("Error while enriching trip's (id={}) end time. Message: {}", messageDto.getId(), ex.getMessage());
        }
    }
}
