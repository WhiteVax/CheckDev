package ru.job4j.site.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import ru.job4j.site.service.EurekaUriProvider;

/**
 * CheckDev пробное собеседование
 * Создание DI объекта WebClient для сервиса auth
 *
 * @author Dmitry Stepanov, user Dmitry
 * @since 19.09.2023
 */
@Configuration
@RequiredArgsConstructor
public class WebClientAuthConfig {

    private final EurekaUriProvider uriProvider;
    private static final String SERVICE_ID = "auth";

    @Bean
    public WebClient createWebClientAuth() {
        return WebClient.create(uriProvider.getUri(SERVICE_ID));
    }
}
