package com.example.app.event.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder(toBuilder = true)
public class WeatherResponseDTO {

  @JsonProperty("properties")
  private Properties properties;

  public Properties getProperties() {
    return properties;
  }

  @Getter
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Properties {

    @JsonProperty("timeseries")
    private List<TimeSeries> timeseries;
  }

  @Getter
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class TimeSeries {

    @JsonProperty("time")
    private String time;

    @JsonProperty("data")
    private Data data;

  }

  @Getter
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Data {

    @JsonProperty("instant")
    private Instant instant;

  }

  @Getter
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Instant {

    @JsonProperty("details")
    private Details details;

  }

  @Getter
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Details {

    @JsonProperty("air_temperature")
    private Double airTemperature;

    @JsonProperty("wind_speed")
    private Double windSpeed;

  }

}
