package com.example.app.event.config;

import com.example.app.event.dto.WeatherResponseDTO;
import java.time.Instant;

public record CachedWeather(
    WeatherResponseDTO weatherResponse,
    Instant lastModified,
    Instant expiresAt
) {}
