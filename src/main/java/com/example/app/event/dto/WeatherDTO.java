package com.example.app.event.dto;

import com.example.app.event.dto.WeatherResponseDTO.TimeSeries;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
  private ZonedDateTime timestamp;

  public static WeatherDTO ofWeatherResponseDTO(WeatherResponseDTO weatherResponseDTO) {
    WeatherResponseDTO.TimeSeries closestToNow = getClosestDetail(weatherResponseDTO);
    return WeatherDTO.builder()
        .timestamp(ZonedDateTime.parse(closestToNow.getTime()))
        .temperature(closestToNow.getData().getInstant().getDetails().getAirTemperature())
        .windSpeed(closestToNow.getData().getInstant().getDetails().getWindSpeed())
        .build();
  }

  private static WeatherResponseDTO.TimeSeries getClosestDetail(WeatherResponseDTO weatherResponseDTO) {
    List<TimeSeries> timeSeries = weatherResponseDTO.getProperties().getTimeseries();
    return timeSeries.stream()
        .filter(ts -> {
          ZonedDateTime time = ZonedDateTime.parse(ts.getTime());
          return time.isAfter(ZonedDateTime.now(ZoneOffset.UTC).minusHours(1));
        })
        .findFirst()
        // TODO: create proper exception
        .orElseThrow();
  }
}
