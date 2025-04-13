package com.example.app.event.service;

import com.example.app.event.dto.EventDTO;
import com.example.app.event.dto.WeatherDTO;
import com.example.app.event.repository.NoopEventRepository;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class EventService {

  private final NoopEventRepository eventRepository;
  private final WeatherService weatherService;

  @Autowired
  public EventService(NoopEventRepository eventRepository, WeatherService weatherService) {
    this.eventRepository = eventRepository;
    this.weatherService = weatherService;
  }

  public Mono<EventDTO> getEventById(UUID eventId) {
    return eventRepository.findById(eventId)
        .flatMap(event -> Mono.just(EventDTO.ofEntity(event)))
        .flatMap(eventDTO -> {
          if (eventDTO.getLocation() != null && LocalDateTime.now().isBefore(eventDTO.getStartDate().plusDays(7))) {
            try {
              return weatherService.fetchWeatherData(eventDTO.getLocation().getLatitude(), eventDTO.getLocation().getLongitude())
                  .map(weatherData -> {
                    WeatherDTO weatherDTO = WeatherDTO.ofWeatherResponseDTO(weatherData);
                    eventDTO.setWeatherForecast(weatherDTO);
                    return eventDTO;
                  });
            } catch (URISyntaxException e) {
              // TODO: proper logging
              return Mono.error(new IllegalArgumentException("Invalid uri was built for eventId: " + eventId));
            }
          } else {
            return Mono.just(eventDTO);
          }
        });
  }
}
