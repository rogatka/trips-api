package com.example.trips.application.rest;

import com.example.trips.domain.exception.InternalServerErrorException;
import com.example.trips.domain.exception.NotFoundException;
import com.example.trips.domain.exception.ValidationException;
import com.example.trips.domain.model.EventType;
import com.example.trips.domain.model.Trip;
import com.example.trips.domain.model.TripCreateDto;
import com.example.trips.domain.model.TripMessageDto;
import com.example.trips.domain.processor.TripMessageProcessorAggregator;
import com.example.trips.domain.service.TripService;
import com.example.trips.infrastracture.configuration.properties.AuthenticationProperties;
import com.example.trips.infrastracture.processor.rabbitmq.RabbitFinishTripMessageProcessor;
import com.example.trips.infrastracture.processor.rabbitmq.RabbitStartTripMessageProcessor;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TripController.class)
class TripControllerTest {
    private static final LocalDateTime CREATION_TIME = LocalDateTime.of(2021, 12, 25, 1, 1, 1, 1);
    private static final LocalDateTime START_TIME = LocalDateTime.of(2021, 12, 26, 1, 1, 1, 1);
    private static final LocalDateTime END_TIME = LocalDateTime.of(2021, 12, 27, 1, 1, 1, 1);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TripService tripService;

    @MockBean
    private TripMessageProcessorAggregator tripMessageProcessorAggregator;

    @SpyBean
    private AuthenticationProperties authenticationProperties;

    @Test
    void shouldReturn_403_Forbidden_IfInvalidBearerToken() throws Exception {
        String id = "test";
        mockMvc.perform(get("/trips/{id}", id)
                        .header("Authorization", "Bearer " + "invalid token"))
                .andExpect(status().isForbidden())
                .andExpect(status().reason(containsString("Invalid Bearer token value")));
    }

    @Test
    void shouldReturn_404_NotFound_IfTripNotFoundById() throws Exception {
        String id = "test";
        String notFoundMessage = String.format("Trip with id=%s not found", id);
        NotFoundException notFoundException = new NotFoundException(notFoundMessage);
        when(tripService.findById(id)).thenThrow(notFoundException);

        mockMvc.perform(get("/trips/{id}", id)
                        .header("Authorization", "Bearer " + authenticationProperties.getSecret()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$", is(notFoundMessage)));
    }

    @Test
    void shouldReturnTrip_And_200_OK_IfTripFoundById() throws Exception {
        String id = "test";
        Trip trip = new Trip();
        trip.setId(id);
        trip.setStartDestination("Moscow");
        trip.setFinalDestination("Paris");
        trip.setOwnerEmail("test@mail.com");
        trip.setDateCreated(CREATION_TIME);
        trip.setStartTime(START_TIME);
        trip.setEndTime(END_TIME);
        when(tripService.findById(id)).thenReturn(trip);

        mockMvc.perform(get("/trips/{id}", id)
                        .header("Authorization", "Bearer " + authenticationProperties.getSecret()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(trip.getId())))
                .andExpect(jsonPath("$.startDestination", is(trip.getStartDestination())))
                .andExpect(jsonPath("$.finalDestination", is(trip.getFinalDestination())))
                .andExpect(jsonPath("$.startTime", is(trip.getStartTime().toString())))
                .andExpect(jsonPath("$.endTime", is(trip.getEndTime().toString())))
                .andExpect(jsonPath("$.dateCreated", is(trip.getDateCreated().toString())))
                .andExpect(jsonPath("$.ownerEmail", is(trip.getOwnerEmail())));
    }

    @Test
    void shouldReturnEmptyCollectionInResponse_200_OK_IfTripsNotFoundByEmail() throws Exception {
        String email = "test@mail.com";
        when(tripService.findAllByEmail(email)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/trips")
                        .queryParam("email", email)
                        .header("Authorization", "Bearer " + authenticationProperties.getSecret()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(empty())));
    }

    @Test
    void shouldReturnAllTrips_200_OK_IfTripsFoundByEmail() throws Exception {
        String email = "test@mail.com";
        List<Trip> trips = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Trip trip = new Trip();
            trip.setId(String.valueOf(i));
            trips.add(trip);
        }
        when(tripService.findAllByEmail(email)).thenReturn(trips);

        mockMvc.perform(get("/trips")
                        .queryParam("email", email)
                        .header("Authorization", "Bearer " + authenticationProperties.getSecret()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(trips.size())))
                .andExpect(jsonPath("$[0].id", is(equalTo(trips.get(0).getId()))));
    }

    @Test
    void shouldReturn400_BadRequest_IfValidationFailedOnTripCreation() throws Exception {
        TripCreateDto tripCreateDto = new TripCreateDto();
        when(tripService.create(tripCreateDto)).thenThrow(ValidationException.class);

        mockMvc.perform(post("/trips")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{}")
                        .header("Authorization", "Bearer " + authenticationProperties.getSecret()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnCreatedTrip_And_200_OK_OnSuccessfulTripCreation() throws Exception {
        TripCreateDto tripCreateDto = new TripCreateDto();
        tripCreateDto.setOwnerEmail("test@mail.com");
        tripCreateDto.setStartDestination("Moscow");
        tripCreateDto.setFinalDestination("Paris");

        Trip trip = new Trip();
        trip.setId("test");
        when(tripService.create(tripCreateDto)).thenReturn(trip);

        mockMvc.perform(post("/trips")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\n" +
                                "    \"startDestination\": \"Moscow\",\n" +
                                "    \"finalDestination\": \"Paris\",\n" +
                                "    \"ownerEmail\": \"test@mail.com\"\n" +
                                "}")
                        .header("Authorization", "Bearer " + authenticationProperties.getSecret()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(trip.getId())));
    }

    @Test
    void shouldReturn_404_NotFound_OnTripDeletion_IfTripNotFound() throws Exception {
        String id = "test";
        String notFoundMessage = String.format("Trip with id=%s not found", id);
        NotFoundException notFoundException = new NotFoundException(notFoundMessage);
        doThrow(notFoundException).when(tripService).deleteById(id);

        mockMvc.perform(delete("/trips/{id}", id)
                        .header("Authorization", "Bearer " + authenticationProperties.getSecret()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$", is(notFoundMessage)));
    }

    @Test
    void shouldReturn_204_NoContent_OnSuccessfulTripDeletion() throws Exception {
        String id = "test";
        mockMvc.perform(delete("/trips/{id}", id)
                        .header("Authorization", "Bearer " + authenticationProperties.getSecret()))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn_404_NotFound_OnTripStart_IfTripNotFound() throws Exception {
        String id = "test";
        String notFoundMessage = String.format("Trip with id=%s not found", id);
        NotFoundException notFoundException = new NotFoundException(notFoundMessage);
        doThrow(notFoundException).when(tripService).findById(id);

        mockMvc.perform(patch("/trips/{id}/start", id)
                        .header("Authorization", "Bearer " + authenticationProperties.getSecret()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$", is(notFoundMessage)));
    }

    @Test
    void shouldReturn_500_InternalServerError_IfProcessorNotFoundForEventType() throws Exception {
        String id = "test";
        Trip trip = new Trip();
        trip.setId(id);
        trip.setStartDestination("Moscow");
        trip.setFinalDestination("Paris");
        trip.setOwnerEmail("test@mail.com");
        trip.setDateCreated(CREATION_TIME);
        trip.setStartTime(START_TIME);
        trip.setEndTime(END_TIME);
        when(tripService.findById(id)).thenReturn(trip);

        doThrow(IllegalStateException.class).when(tripMessageProcessorAggregator).getProcessorForEventType(EventType.START_TRIP);

        mockMvc.perform(patch("/trips/{id}/start", id)
                        .header("Authorization", "Bearer " + authenticationProperties.getSecret()))
                .andExpect(status().isInternalServerError());

        doThrow(IllegalStateException.class).when(tripMessageProcessorAggregator).getProcessorForEventType(EventType.FINISH_TRIP);
        mockMvc.perform(patch("/trips/{id}/finish", id)
                        .header("Authorization", "Bearer " + authenticationProperties.getSecret()))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldReturn_500_InternalServerError_IfInternalServerErrorExceptionInProcessor() throws Exception {
        String id = "test";
        Trip trip = new Trip();
        trip.setId(id);
        trip.setStartDestination("Moscow");
        trip.setFinalDestination("Paris");
        trip.setOwnerEmail("test@mail.com");
        trip.setDateCreated(CREATION_TIME);
        trip.setStartTime(START_TIME);
        trip.setEndTime(END_TIME);
        when(tripService.findById(id)).thenReturn(trip);

        doThrow(InternalServerErrorException.class).when(tripMessageProcessorAggregator).getProcessorForEventType(EventType.START_TRIP);

        mockMvc.perform(patch("/trips/{id}/start", id)
                        .header("Authorization", "Bearer " + authenticationProperties.getSecret()))
                .andExpect(status().isInternalServerError());

        doThrow(InternalServerErrorException.class).when(tripMessageProcessorAggregator).getProcessorForEventType(EventType.FINISH_TRIP);
        mockMvc.perform(patch("/trips/{id}/finish", id)
                        .header("Authorization", "Bearer " + authenticationProperties.getSecret()))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldReturn_204_NoContent_OnSuccessfulTripStart_OrTripFinish() throws Exception {
        String id = "test";
        Trip trip = new Trip();
        trip.setId(id);
        trip.setStartDestination("Moscow");
        trip.setFinalDestination("Paris");
        trip.setOwnerEmail("test@mail.com");
        trip.setDateCreated(CREATION_TIME);
        trip.setStartTime(START_TIME);
        trip.setEndTime(END_TIME);
        when(tripService.findById(id)).thenReturn(trip);

        RabbitStartTripMessageProcessor rabbitStartTripMessageProcessor = mock(RabbitStartTripMessageProcessor.class);
        RabbitFinishTripMessageProcessor rabbitFinishTripMessageProcessor = mock(RabbitFinishTripMessageProcessor.class);
        doNothing().when(rabbitStartTripMessageProcessor).process(any(TripMessageDto.class));
        doNothing().when(rabbitFinishTripMessageProcessor).process(any(TripMessageDto.class));
        when(tripMessageProcessorAggregator.getProcessorForEventType(EventType.START_TRIP)).thenReturn(rabbitStartTripMessageProcessor);
        when(tripMessageProcessorAggregator.getProcessorForEventType(EventType.FINISH_TRIP)).thenReturn(rabbitFinishTripMessageProcessor);

        mockMvc.perform(patch("/trips/{id}/start", id)
                        .header("Authorization", "Bearer " + authenticationProperties.getSecret()))
                .andExpect(status().isNoContent());

        mockMvc.perform(patch("/trips/{id}/finish", id)
                        .header("Authorization", "Bearer " + authenticationProperties.getSecret()))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn_404_NotFound_OnTripFinish_IfTripNotFound() throws Exception {
        String id = "test";
        String notFoundMessage = String.format("Trip with id=%s not found", id);
        NotFoundException notFoundException = new NotFoundException(notFoundMessage);
        doThrow(notFoundException).when(tripService).findById(id);

        mockMvc.perform(patch("/trips/{id}/finish", id)
                        .header("Authorization", "Bearer " + authenticationProperties.getSecret()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$", is(notFoundMessage)));
    }
}