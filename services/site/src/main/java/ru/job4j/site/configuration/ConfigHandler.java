package ru.job4j.site.configuration;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.job4j.site.handler.RestTemplateResponseErrorHandler;

@Configuration
@Component
public class ConfigHandler {
    @Bean
    public RestTemplate restTemplate() {
      return new RestTemplateBuilder()
              .errorHandler(new RestTemplateResponseErrorHandler())
              .build();
    }
}
