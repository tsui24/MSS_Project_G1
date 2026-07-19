package com.hotel.billing.client;

import com.hotel.billing.dto.DamageReportDto;
import com.hotel.billing.dto.RoomDto;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;

@Component
public class RoomServiceClient {
    private final WebClient webClient;
    public RoomServiceClient(WebClient.Builder builder){webClient=builder.baseUrl("http://ROOM-SERVICE").build();}
    public RoomDto getRoom(Long id){return webClient.get().uri("/api/catalog/rooms/{id}",id).retrieve().bodyToMono(RoomDto.class).block();}
    public List<DamageReportDto> getDamageReports(Long reservationId){
        DamageReportDto[] result=webClient.get().uri(uri -> uri.path("/api/catalog/damage-reports")
                .queryParam("reservationId",reservationId).build()).retrieve().bodyToMono(DamageReportDto[].class).block();
        return result == null ? List.of() : List.of(result);
    }
}
