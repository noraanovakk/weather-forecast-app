package com.example.app.event.repository;

import static com.example.app.event.mock.MockEvent.EVENT_ID_WITHOUT_LOCATION;
import static com.example.app.event.mock.MockEvent.EVENT_ID_WITH_LOCATION;

import com.example.app.event.mock.MockEvent;
import com.example.app.event.model.Event;
import java.util.UUID;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
// TODO: implement a repository interface which extends ReactiveCrudRepository
public class NoopEventRepository {

  public Mono<Event> findById(UUID id) {
    if(id.equals(EVENT_ID_WITH_LOCATION)) {
      return Mono.just(MockEvent.getMockEventWithLocation());
    } else if(id.equals(EVENT_ID_WITHOUT_LOCATION)) {
      return Mono.just(MockEvent.getMockEventWithoutLocation());
    } else {
      return Mono.empty();
    }
  }
}
