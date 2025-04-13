package com.example.app.event.service;

import com.example.app.event.dto.EventDTO;
import com.example.app.event.repository.NoopEventRepository;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class EventService {

  private final NoopEventRepository eventRepository;

  @Autowired
  public EventService(NoopEventRepository eventRepository) {
    this.eventRepository = eventRepository;
  }

  public Mono<EventDTO> getEventById(UUID eventId) {
//    return eventRepository.findById(eventId); // TODO: map to DTO
    return null;
  }
}
