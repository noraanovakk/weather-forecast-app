package com.example.app.event.service;

import com.example.app.event.dto.WeatherResponseDTO;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import org.apache.hc.core5.net.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class WeatherService {

  private static final String WEATHER_API_URL = "https://api.met.no/weatherapi/locationforecast/2.0/compact";
  public static final String DEMO_USER_AGENT = "DemoApp/1.0 me@example.com";

  private final WebClient webClient;

  @Autowired
  public WeatherService(WebClient.Builder webClientBuilder) {
    this.webClient = webClientBuilder.build();
  }

  public Mono<WeatherResponseDTO> fetchWeatherData(double latitude, double longitude) throws URISyntaxException {

    // TODO: add caching
    return getWeatherResponse(webClient, getRequestHeaders(), latitude, longitude);
  }

  private Mono<WeatherResponseDTO> getWeatherResponse(
      WebClient webClient,
      HttpHeaders requestHeaders,
      double latitude,
      double longitude) throws URISyntaxException {
    return webClient
        .get()
        .uri(getUri(latitude, longitude))
        .headers(requestHeaders::addAll)
        .exchangeToMono(response -> {
          if (response.statusCode().is2xxSuccessful()) {
            return response.bodyToMono(WeatherResponseDTO.class)
                .map(dto -> dto);
          }
          return response.createException().flatMap(Mono::error);
        });
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
    // TODO replace with actual user agent
    headers.add(HttpHeaders.USER_AGENT, DEMO_USER_AGENT);
    return headers;
  }
}
