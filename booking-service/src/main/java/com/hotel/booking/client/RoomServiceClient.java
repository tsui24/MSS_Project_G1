package com.hotel.booking.client;

import com.hotel.booking.dto.RoomDto;
import com.hotel.booking.dto.RoomPageDto;
import com.hotel.booking.exception.ResourceNotFoundException;
import com.hotel.booking.exception.InvalidStateException;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
public class RoomServiceClient {

    private final WebClient webClient;

    public RoomServiceClient(WebClient.Builder loadBalancedWebClientBuilder) {
        this.webClient = loadBalancedWebClientBuilder.baseUrl("http://ROOM-SERVICE").build();
    }

    public RoomDto getRoom(Long roomId) {
        return webClient.get()
                .uri("/api/catalog/rooms/{id}", roomId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        Mono.error(new ResourceNotFoundException("Room not found with id: " + roomId)))
                .bodyToMono(RoomDto.class)
                .block();
    }

    /** Pulls one large page (up to 500 rooms) since this is used for availability checks, not paged browsing. */
    public List<RoomDto> getRooms(Long roomClassId) {
        RoomPageDto page = webClient.get()
                .uri(uriBuilder -> {
                    UriBuilder builder = uriBuilder.path("/api/catalog/rooms").queryParam("size", 500);
                    if (roomClassId != null) {
                        builder.queryParam("roomClassId", roomClassId);
                    }
                    return builder.build();
                })
                .retrieve()
                .bodyToMono(RoomPageDto.class)
                .block();
        return page != null && page.getContent() != null ? page.getContent() : List.of();
    }

    public void updateRoomStatus(Long roomId, String status) {
        webClient.patch()
                .uri("/api/catalog/rooms/{id}/status", roomId)
                .bodyValue(Map.of("status", status))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        Mono.error(new ResourceNotFoundException("Room not found with id: " + roomId)))
                .bodyToMono(Void.class)
                .block();
    }

    public void compensateFailedCheckIn(Long roomId) {
        webClient.patch()
                .uri("/api/catalog/rooms/{id}/status/compensate-check-in", roomId)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public void occupyIfAvailable(Long roomId) {
        webClient.patch()
                .uri("/api/catalog/rooms/{id}/status/occupy-if-available", roomId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        Mono.error(new InvalidStateException("Room " + roomId + " is no longer AVAILABLE")))
                .bodyToMono(Void.class)
                .block();
    }
}
