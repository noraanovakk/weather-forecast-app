package com.example.app.event.api;

import com.example.app.event.dto.EventDTO;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface EventAPI {

  /**
   * Get an event by id
   * @param eventId The id of the event
   * @return The event
   */
  Mono<ResponseEntity<EventDTO>> getEventById(UUID eventId);

}
