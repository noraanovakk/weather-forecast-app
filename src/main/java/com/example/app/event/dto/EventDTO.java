package com.example.app.event.dto;

import com.example.app.event.model.Event;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL) // Excludes nulls from the JSON
public class EventDTO {

  @JsonProperty("id")
  private UUID id;

  @JsonProperty("name")
  private String name;

  @JsonProperty("startDate")
  private ZonedDateTime startDate;

  @JsonProperty("endDate")
  private ZonedDateTime endDate;

  @JsonProperty("location")
  @Nullable
  private LocationDTO location;

  // might cache the weather data for a while
  @JsonProperty("weatherForecast")
  private WeatherDTO weatherForecast;

  public static EventDTO ofEntity(Event event) {
    return EventDTO.builder()
        .id(event.getId())
        .name(event.getName())
        .startDate(ZonedDateTime.parse(event.getStartDate()))
        .endDate(ZonedDateTime.parse(event.getEndDate()))
        .location(Optional.ofNullable(event.getLocation())
            .map(LocationDTO::ofEntity)
            .orElse(null))
        .build();
  }
}
