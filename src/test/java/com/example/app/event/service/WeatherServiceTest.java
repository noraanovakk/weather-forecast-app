package com.example.app.event.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.example.app.event.config.CachedWeather;
import com.example.app.event.dto.WeatherResponseDTO;
import com.github.benmanes.caffeine.cache.Cache;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {
  @Mock
  private Cache<String, CachedWeather> weatherCache;

  @Mock
  private WebClient.Builder webClientBuilder;

  @Mock
  private WebClient webClient;

  @Mock
  private WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpec;

  @Mock
  private WebClient.RequestHeadersSpec<?> requestHeadersSpec;

  @Mock
  private WebClient.ResponseSpec responseSpec;

  private WeatherService weatherService;

  private static final double LAT = 59.9139;
  private static final double LON = 10.7522;
  private static final String CACHE_KEY = "lat:59.9139,lon:10.7522";

  @BeforeEach
  void setUp() {
    when(webClientBuilder.build()).thenReturn(webClient);
    requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
    requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
    weatherService = new WeatherService(weatherCache, webClientBuilder);
  }

  @Test
  void shouldReturnCachedWeatherIfValid() throws Exception {
    // Given
    Instant now = Instant.now();
    WeatherResponseDTO dto = WeatherResponseDTO.builder().build();
    CachedWeather cached = new CachedWeather(dto, now.minusSeconds(60), now.plusSeconds(3600));

    when(weatherCache.getIfPresent(CACHE_KEY)).thenReturn(cached);

    // When & Then
    StepVerifier.create(weatherService.fetchWeatherData(LAT, LON))
        .expectNext(dto)
        .verifyComplete();

    verifyNoInteractions(webClient); // Should not call external API
  }

  // TODO: Add more test cases
}
