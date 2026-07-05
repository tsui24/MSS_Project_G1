package com.hotel.booking.client;

import com.hotel.booking.dto.AuthUserDto;
import com.hotel.booking.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class AuthServiceClient {

    private final WebClient webClient;

    public AuthServiceClient(WebClient.Builder loadBalancedWebClientBuilder) {
        this.webClient = loadBalancedWebClientBuilder.baseUrl("http://AUTH-SERVICE").build();
    }

    public AuthUserDto getUser(Long customerId) {
        return webClient.get()
                .uri("/api/auth/users/{id}", customerId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        Mono.error(new ResourceNotFoundException("Customer not found with id: " + customerId)))
                .bodyToMono(AuthUserDto.class)
                .block();
    }
}
