package com.example.app.event.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
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
  private LocalDateTime startDate;

  @JsonProperty("endDate")
  private LocalDateTime endDate;

  @JsonProperty("location")
  private LocationDTO location;

  // might cache the weather data for a while
  @JsonProperty("weatherForecast")
  private WeatherDTO weatherForecast;


}
