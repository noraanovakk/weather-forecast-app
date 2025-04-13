package com.example.app.event.dto;

import com.example.app.event.dto.WeatherResponseDTO.TimeSeries;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
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

  public static WeatherDTO ofWeatherResponseDTO(WeatherResponseDTO weatherResponseDTO) {
    WeatherResponseDTO.TimeSeries closestToNow = getClosestDetail(weatherResponseDTO);
    return WeatherDTO.builder()
        .timestamp(LocalDateTime.ofInstant(Instant.parse(closestToNow.getTime()), ZoneOffset.UTC))
        .temperature(closestToNow.getData().getInstant().getDetails().getAirTemperature())
        .windSpeed(closestToNow.getData().getInstant().getDetails().getWindSpeed())
        .build();
  }

  private static WeatherResponseDTO.TimeSeries getClosestDetail(WeatherResponseDTO weatherResponseDTO) {
    List<TimeSeries> timeSeries = weatherResponseDTO.getProperties().getTimeseries();
    return timeSeries.stream()
        .filter(ts -> {
          LocalDateTime time = LocalDateTime.ofInstant(Instant.parse(ts.getTime()), ZoneOffset.UTC);
          return time.isAfter(LocalDateTime.now().minusHours(1));
        })
        .findFirst()
        // TODO: create proper exception
        .orElseThrow();
  }
}
