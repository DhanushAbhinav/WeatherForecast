package com.example.weather.service;

import com.example.weather.model.Weather;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class WeatherService {

    @Value("${weatherstack.api.key}")
    private String apiKey;

    @Value("${weatherstack.api.url}")
    private String apiUrl;

    public Weather getWeather(String city) {
        RestTemplate restTemplate = new RestTemplate();
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("access_key", apiKey)
                .queryParam("query", city);

        String response = restTemplate.getForObject(uriBuilder.toUriString(), String.class);
        
        // Parse the response JSON and map it to Weather object
        return parseWeatherFromJson(response);
    }

    private Weather parseWeatherFromJson(String response) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(response);
            JsonNode current = root.path("current");
            Weather weather = new Weather();
            weather.setLocation(root.path("location").path("name").asText());
            weather.setTemperature(current.path("temperature").asText());
            weather.setDescription(current.path("weather_descriptions").get(0).asText());
            return weather;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}