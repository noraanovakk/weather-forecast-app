package com.example.app.event.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder(toBuilder = true)
public class LocationDTO {

  @JsonProperty("locationId")
  private UUID locationId;

  @JsonProperty("name")
  private String name;

  @JsonProperty("altitude")
  private double altitude;

  @JsonProperty("latitude")
  private double latitude;

  @JsonProperty("longitude")
  private double longitude;
}
