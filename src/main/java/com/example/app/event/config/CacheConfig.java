package com.example.app.event.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {
  @Bean
  public Cache<String, CachedWeather> weatherCache() {
    // Caffeine for in-memory caching
    return Caffeine.newBuilder()
        .expireAfterWrite(2, TimeUnit.HOURS) // max data age
        .maximumSize(1000) // adjust based on the needs
        .build();
  }
}
