# Event Weather API

This is a Maven-based **Spring Boot** application built using **Spring WebFlux** (reactive stack). It exposes a REST API endpoint that returns event information with optional weather forecast data.

## 📌 Features

- Reactive REST endpoint using **Spring WebFlux**
- Fetches weather data from [api.met.no](https://api.met.no/)
- Caches weather data using in-memory **Caffeine** cache (expires after max. 2 hours)
- Returns weather information **only if**:
    - The event has a location
    - The event is within 7 days from the current time
- Some basic **unit tests** are included

---

## 📡 API Endpoint

### `GET /events/{eventId}`

Returns an `EventDTO` with optional weather information.

#### ✅ Example Response

```json
{
  "id": "65b80d97-ecd7-4c75-96d0-177b45843cf6",
  "name": "Sample Event With Location",
  "startDate": "2025-04-13T14:15:15.219692Z",
  "endDate": "2025-04-13T17:15:15.219732Z",
  "location": {
    "locationId": "9715ea67-32de-47e7-9a7a-9c1721901ffb",
    "name": "Fotball field",
    "altitude": 0.0,
    "latitude": 59.9299837,
    "longitude": 10.7949928
  },
  "weatherForecast": {
    "temperature": 15.9,
    "windSpeed": 3.1,
    "timestamp": "2025-04-13T15:00:00Z"
  }
}
```

---

## 🚀 Running Locally

### Prerequisites

- Java 17+
- Maven 3.8+
- Internet connection (to call the weather API)

### Steps

```bash
# Clone the repo
git clone https://github.com/your-org/event-weather-api.git
cd event-weather-api

# Build the app
mvn clean install

# Run the app
mvn spring-boot:run
```

The application should be accessible at: `http://localhost:8080`

---

## 🧪 Testing with Postman

1. Run the application

2. Import the following GET request into Postman:

   ```
   GET http://localhost:8080/events/{eventId}
   ```

3. Replace `{eventId}` with an actual UUID that exists in Mock data (can be found within `src/main/java/com/example/app/event/mock/MockEvent.java`).

> Note: The app uses a dummy/in-memory event repository. You may want to preload or mock some data for testing.

---

## 🛠️ Future Improvements

### Logging & Observability

- Implement structured logging with **SLF4J / Logback**
- Add **exception handling** via `@ControllerAdvice`
- Integrate metrics using **Micrometer** + Prometheus

### Caching (Cloud Optimization)

- Move from in-memory **Caffeine** to:
    - **Redis** (centralized and scalable)
    - Or use a managed caching service in the cloud (e.g., AWS ElastiCache, Azure Redis)
- Add support for **time-based eviction** and **manual invalidation**

### Scalability & Load Balancing

- Deploy in a Kubernetes cluster
- Place behind a **load balancer** (e.g., NGINX ingress or cloud-native LB)
- Use **Rate Limiting**

### Testing

- Add **integration tests** with WebTestClient
- Add **contract testing** if consuming third-party APIs
- Mock weather API in tests for deterministic behavior

---

## 💡 License & Attribution

- Weather data provided by [Meteorologisk institutt (api.met.no)](https://api.met.no/doc/TermsOfService)
- Please adhere to their **Terms of Service** when deploying this app publicly.

---

Made by [Nora Novak]

