package com.example.app.event.service;

import com.example.app.event.dto.EventDTO;
import com.example.app.event.dto.LocationDTO;
import com.example.app.event.dto.WeatherDTO;
import com.example.app.event.dto.WeatherResponseDTO;
import com.example.app.event.model.Event;
import com.example.app.event.model.Location;
import com.example.app.event.repository.NoopEventRepository;
import java.net.URISyntaxException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {
  @Mock
  private NoopEventRepository eventRepository;

  @Mock
  private WeatherService weatherService;

  private EventService eventService;

  @BeforeEach
  void setUp() {
    eventService = new EventService(eventRepository, weatherService);
  }

  @Test
  void testGetEventById_whenEventHasLocationAndIsUpcoming_shouldIncludeWeather() throws Exception {
    // Given
    UUID eventId = UUID.randomUUID();
    UUID locationId = UUID.randomUUID();
    Location location = Location.builder()
        .id(locationId)
        .name("Oslo football stadium")
        .latitude(59.0)
        .longitude(10.0)
        .build();

    Event event = new Event();
    event.setId(eventId);
    event.setStartDate(ZonedDateTime.now(ZoneOffset.UTC).plusDays(2).toString());
    event.setEndDate(ZonedDateTime.now(ZoneOffset.UTC).plusDays(2).plusHours(2).toString());
    event.setLocation(location);

    EventDTO eventDTO = EventDTO.builder()
        .id(eventId)
        .startDate(ZonedDateTime.parse(event.getStartDate()))
        .endDate(ZonedDateTime.parse(event.getEndDate()))
        .location(LocationDTO.builder()
            .locationId(location.getId())
            .name(location.getName())
            .altitude(location.getAltitude())
            .latitude(location.getLatitude())
            .longitude(location.getLongitude())
            .build())
        .build();

    WeatherResponseDTO weatherResponse = WeatherResponseDTO.builder().build();
    WeatherDTO weatherDTO = WeatherDTO.builder().build();

    // When
    Mockito.when(eventRepository.findById(eventId)).thenReturn(Mono.just(event));
    Mockito.mockStatic(EventDTO.class).when(() -> EventDTO.ofEntity(event)).thenReturn(eventDTO);
    Mockito.when(weatherService.fetchWeatherData(59.0, 10.0)).thenReturn(Mono.just(weatherResponse));
    Mockito.mockStatic(WeatherDTO.class).when(() -> WeatherDTO.ofWeatherResponseDTO(weatherResponse)).thenReturn(weatherDTO);

    // Then
    StepVerifier.create(eventService.getEventById(eventId))
        .expectNextMatches(result -> result.getWeatherForecast() == weatherDTO)
        .verifyComplete();
  }

  // TODO: fix tests using try with resources
  @Test
  void testGetEventById_whenEventHasNoLocation_shouldNotFetchWeather() throws URISyntaxException {
    UUID eventId = UUID.randomUUID();

    Event event = new Event();
    event.setId(eventId);
    event.setStartDate(ZonedDateTime.now(ZoneOffset.UTC).plusDays(1).toString());
    event.setEndDate(ZonedDateTime.now(ZoneOffset.UTC).plusDays(1).plusHours(2).toString());
    event.setName("Test event");
    event.setLocation(null);

    EventDTO eventDTO = EventDTO.builder()
        .id(eventId)
        .startDate(ZonedDateTime.parse(event.getStartDate()))
        .endDate(ZonedDateTime.parse(event.getEndDate()))
        .name(event.getName())
        .location(null)
        .build();

    Mockito.when(eventRepository.findById(eventId)).thenReturn(Mono.just(event));
    Mockito.mockStatic(EventDTO.class).when(() -> EventDTO.ofEntity(event)).thenReturn(eventDTO); // TODO: fix when running all tests

    StepVerifier.create(eventService.getEventById(eventId))
        .expectNext(eventDTO)
        .verifyComplete();

    Mockito.verify(weatherService, Mockito.never()).fetchWeatherData(Mockito.any(), Mockito.any());
  }

  @Test
  void testGetEventById_whenEventIsTooFar_shouldNotFetchWeather() throws URISyntaxException {
    UUID eventId = UUID.randomUUID();
    UUID locationId = UUID.randomUUID();

    Event event = new Event();
    event.setId(eventId);
    event.setStartDate(ZonedDateTime.now(ZoneOffset.UTC).plusDays(8).toString());
    event.setEndDate(ZonedDateTime.now(ZoneOffset.UTC).plusDays(8).plusHours(2).toString());
    event.setLocation(Location.builder()
        .id(locationId)
        .latitude(59.0)
        .longitude(10.0)
        .build());

    EventDTO eventDTO = EventDTO.builder()
        .id(eventId)
        .startDate(ZonedDateTime.parse(event.getStartDate()))
        .endDate(ZonedDateTime.parse(event.getEndDate()))
        .location(LocationDTO.builder()
            .locationId(locationId)
            .latitude(event.getLocation().getLatitude())
            .longitude(event.getLocation().getLongitude())
            .build())
        .build();

    Mockito.when(eventRepository.findById(eventId)).thenReturn(Mono.just(event));
    Mockito.mockStatic(EventDTO.class).when(() -> EventDTO.ofEntity(event)).thenReturn(eventDTO); // TODO: fix when running all tests

    StepVerifier.create(eventService.getEventById(eventId))
        .expectNext(eventDTO)
        .verifyComplete();

    Mockito.verify(weatherService, Mockito.never()).fetchWeatherData(Mockito.any(), Mockito.any());
  }

  @Test
  void testGetEventById_whenEventNotFound_shouldReturnEmptyMono() {
    UUID eventId = UUID.randomUUID();

    Mockito.when(eventRepository.findById(eventId)).thenReturn(Mono.empty());

    StepVerifier.create(eventService.getEventById(eventId))
        .verifyComplete(); // No value, completes empty
  }
}
