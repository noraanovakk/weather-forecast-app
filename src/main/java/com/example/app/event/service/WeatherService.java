package com.example.app.event.service;

import com.example.app.event.config.CachedWeather;
import com.example.app.event.dto.WeatherResponseDTO;
import com.github.benmanes.caffeine.cache.Cache;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.hc.core5.net.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class WeatherService {

  private static final String WEATHER_API_URL = "https://api.met.no/weatherapi/locationforecast/2.0/compact";
  public static final String DEMO_USER_AGENT = "DemoApp/1.0 me@example.com"; // This should be replaced with a real user agent string
  private static final String CACHE_KEY_FORMAT = "lat:%s,lon:%s";

  private final Cache<String, CachedWeather> weatherCache;
  private final WebClient webClient;

  @Autowired
  public WeatherService(Cache<String, CachedWeather> weatherCache, WebClient.Builder webClientBuilder) {
    this.weatherCache = weatherCache;
    this.webClient = webClientBuilder.build();
  }

  public Mono<WeatherResponseDTO> fetchWeatherData(double latitude, double longitude) throws URISyntaxException {
    String cacheKey = String.format(CACHE_KEY_FORMAT, latitude, longitude);
    CachedWeather cached = weatherCache.getIfPresent(cacheKey);

    if (cached != null && Instant.now().isBefore(cached.expiresAt())) {
      // Still valid – use cached
      return Mono.just(cached.weatherResponse());
    }

    HttpHeaders requestHeaders = getRequestHeaders();
    // TODO: set up monitoring to track the number of requests sent to api.met.no
    //  and implement alerts for any anomalies or potential breaches of the Terms of Service.

    // Add If-Modified-Since only if we have it
    if (cached != null && cached.lastModified() != null) {
      String ifModifiedSince = DateTimeFormatter.RFC_1123_DATE_TIME
          .withZone(ZoneOffset.UTC)
          .format(cached.lastModified());
      requestHeaders.add(HttpHeaders.IF_MODIFIED_SINCE, ifModifiedSince);
    }

    return getWeatherResponse(webClient, requestHeaders, latitude, longitude, cached, cacheKey);
  }

  private Mono<WeatherResponseDTO> getWeatherResponse(
      WebClient webClient,
      HttpHeaders requestHeaders,
      double latitude,
      double longitude,
      CachedWeather cached,
      String cacheKey) throws URISyntaxException {
    return webClient
        .get()
        .uri(getUri(latitude, longitude))
        .headers(requestHeaders::addAll)
        .exchangeToMono(response -> {
          if (response.statusCode() == HttpStatus.NOT_MODIFIED) {
            // Update expiry if Expires header is present
            String expiresHeader = response.headers().asHttpHeaders().getFirst(HttpHeaders.EXPIRES);
            if (expiresHeader != null && cached != null) {
              Instant expiresAt = ZonedDateTime.parse(expiresHeader, DateTimeFormatter.RFC_1123_DATE_TIME).toInstant();
              weatherCache.put(cacheKey, new CachedWeather(cached.weatherResponse(), cached.lastModified(), expiresAt));
            }
            return Mono.justOrEmpty(cached != null ? cached.weatherResponse() : null);
          }
          if (response.statusCode().is2xxSuccessful()) {
            return response.bodyToMono(WeatherResponseDTO.class)
                .map(dto -> {
                  cacheLatestDates(cacheKey, response, dto);
                  return dto;
                });
          }
          return response.createException().flatMap(Mono::error);
        });
  }

  private void cacheLatestDates(String cacheKey, ClientResponse response, WeatherResponseDTO dto) {
    HttpHeaders headers = response.headers().asHttpHeaders();
    Instant lastModified = null;
    Instant expires = Instant.now().plus(Duration.ofHours(2)); // Fallback

    String lastModifiedHeader = headers.getFirst(HttpHeaders.LAST_MODIFIED);
    String expiresHeader = headers.getFirst(HttpHeaders.EXPIRES);

    if (lastModifiedHeader != null) {
      lastModified = ZonedDateTime.parse(lastModifiedHeader, DateTimeFormatter.RFC_1123_DATE_TIME).toInstant();
    }

    if (expiresHeader != null) {
      expires = ZonedDateTime.parse(expiresHeader, DateTimeFormatter.RFC_1123_DATE_TIME).toInstant();
    }

    weatherCache.put(cacheKey, new CachedWeather(dto, lastModified, expires));
  }

  private static String getUri(double latitude, double longitude) throws URISyntaxException {
    URIBuilder uriBuilder = new URIBuilder(WEATHER_API_URL);
    // Truncate latitude and longitude to 4 decimal places
    uriBuilder.addParameter("lat", new DecimalFormat("#.####").format(latitude));
    uriBuilder.addParameter("lon", new DecimalFormat("#.####").format(longitude));
    return uriBuilder.toString();
  }

  private static HttpHeaders getRequestHeaders() {
    HttpHeaders headers = new org.springframework.http.HttpHeaders();
    headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
    headers.add(HttpHeaders.USER_AGENT, DEMO_USER_AGENT);
    return headers;
  }
}
