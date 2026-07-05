package com.hotel.billing.client;

import com.hotel.billing.dto.ReservationDto;
import com.hotel.billing.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class BookingServiceClient {

    private final WebClient webClient;

    public BookingServiceClient(WebClient.Builder loadBalancedWebClientBuilder) {
        this.webClient = loadBalancedWebClientBuilder.baseUrl("http://BOOKING-SERVICE").build();
    }

    public ReservationDto getReservation(Long reservationId) {
        return webClient.get()
                .uri("/api/bookings/reservations/{id}", reservationId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        Mono.error(new ResourceNotFoundException("Reservation not found with id: " + reservationId)))
                .bodyToMono(ReservationDto.class)
                .block();
    }
}
