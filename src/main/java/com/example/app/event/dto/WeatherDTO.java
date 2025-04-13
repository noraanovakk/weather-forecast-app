package com.example.app.event.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder(toBuilder = true)
public class WeatherDTO {

  @JsonProperty("temperature")
  private double temperature;

  @JsonProperty("windSpeed")
  private double windSpeed;

  @JsonProperty("timestamp")
  private LocalDateTime timestamp;
}
