package com.example.app.event.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.example.app.event.api.EventController;
import com.example.app.event.dto.EventDTO;
import com.example.app.event.service.EventService;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {
  @Mock
  private EventService eventService;

  private EventController eventController;

  private WebTestClient webTestClient;

  @BeforeEach
  void setUp() {
    eventController = new EventController(eventService);
    webTestClient = WebTestClient.bindToController(eventController).build();
  }

  @Test
  void testGetEventById_WhenFound_ReturnsEventDTO() {
    // Given
    UUID eventId = UUID.randomUUID();
    EventDTO mockDto = EventDTO.builder().id(eventId).name("Sample Event").build();

    Mockito.when(eventService.getEventById(eventId)).thenReturn(Mono.just(mockDto));

    // When & Then
    webTestClient.get()
        .uri("/events/{id}", eventId)
        .exchange()
        .expectStatus().isOk()
        .expectBody(EventDTO.class)
        .value(dto -> {
          assertThat(dto).isNotNull();
          assertThat(dto.getId()).isEqualTo(eventId);
          assertThat(dto.getName()).isEqualTo("Sample Event");
        });
  }

  @Test
  void testGetEventById_WhenNotFound_Returns404() {
    // Given
    UUID eventId = UUID.randomUUID();

    Mockito.when(eventService.getEventById(eventId)).thenReturn(Mono.empty());

    // When & Then
    webTestClient.get()
        .uri("/events/{id}", eventId)
        .exchange()
        .expectStatus().isNotFound();
  }

}
