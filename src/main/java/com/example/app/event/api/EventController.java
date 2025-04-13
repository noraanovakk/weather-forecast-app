package com.example.app.event.api;

import com.example.app.event.dto.EventDTO;
import com.example.app.event.service.EventService;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/events")
public class EventController implements EventAPI {

  private final EventService eventService;

  @Autowired
  public EventController(EventService eventService) {
    this.eventService = eventService;
  }

  @Override
  @GetMapping("/{eventId}")
  public Mono<ResponseEntity<EventDTO>> getEventById(@PathVariable UUID eventId) {
    return eventService
        .getEventById(eventId)
        .map(ResponseEntity::ok)
        // TODO: proper handling of exceptions thrown by the service
        .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }
}
