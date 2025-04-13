package com.example.app.event.mock;

import com.example.app.event.model.Event;
import com.example.app.event.model.Location;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

public class MockEvent {
  public static final UUID EVENT_ID_WITH_LOCATION = UUID.fromString("65b80d97-ecd7-4c75-96d0-177b45843cf6");
  public static final UUID EVENT_ID_WITH_LOCATION_TENNIS = UUID.fromString("41965f36-5b52-453c-8526-f021919742b8");
  public static final UUID EVENT_ID_WITHOUT_LOCATION = UUID.fromString("b7cab39c-5ecf-48e0-bfdb-27bcaca77d11");

  public static Event getMockEventWithLocation() {
    return Event.builder()
        .id(EVENT_ID_WITH_LOCATION)
        .name("Sample Event With Location")
        .description("This is a sample event description.")
        .startDate(OffsetDateTime.now(ZoneOffset.UTC).minusHours(1).toString())
        .endDate(OffsetDateTime.now(ZoneOffset.UTC).plusHours(2).toString())
        .location(Location.builder()
            .id(UUID.randomUUID())
            .name("Fotball field")
            .latitude(59.9299837)
            .longitude(10.7949928)
            .build())
        .build();
  }

  public static Event getMockEventWithLocationTennis() {
    return Event.builder()
        .id(EVENT_ID_WITH_LOCATION_TENNIS)
        .name("Sample Event With Location")
        .description("This is a sample event description.")
        .startDate(OffsetDateTime.now(ZoneOffset.UTC).plusDays(1).toString())
        .endDate(OffsetDateTime.now(ZoneOffset.UTC).plusDays(1).plusHours(2).toString())
        .location(Location.builder()
            .id(UUID.randomUUID())
            .name("Fotball field")
            .latitude(59.9247312)
            .longitude(10.7963689)
            .build())
        .build();
  }

  public static Event getMockEventWithoutLocation() {
    return Event.builder()
        .id(EVENT_ID_WITHOUT_LOCATION)
        .name("Sample Event Without Location")
        .description("This is a sample event description.")
        .startDate(OffsetDateTime.now(ZoneOffset.UTC).plusDays(3).toString())
        .endDate(OffsetDateTime.now(ZoneOffset.UTC).plusDays(3).plusHours(2).toString())
        .build();
  }
}
