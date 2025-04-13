package com.example.app.event.model;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// TODO: add proper annotations that represents the database table
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Location {

  private UUID id;

  private String name;

  private double altitude;

  private double latitude;

  private double longitude;

}
