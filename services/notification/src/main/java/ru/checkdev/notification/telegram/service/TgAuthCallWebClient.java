package ru.checkdev.notification.telegram.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.checkdev.notification.config.CircuitBreaker;
import ru.checkdev.notification.domain.Profile;

/**
 * Класс реализует методы get и post для отправки сообщений через WebClient
 *
 * @author Dmitry Stepanov, user Dmitry
 * @since 12.09.2023
 */
@Slf4j
@Service
@org.springframework.context.annotation.Profile("default")
public class TgAuthCallWebClient implements TgCall {

    @Value("${server.auth}")
    private String urlServiceAuth;

    private final WebClient webClient;
    private final CircuitBreaker circuitBreaker;

    public TgAuthCallWebClient() {
        this.webClient = null;
        this.circuitBreaker = new CircuitBreaker(3);
    }

    public TgAuthCallWebClient(WebClient webClient, int retries, long delay) {
        this.webClient = webClient;
        this.circuitBreaker = new CircuitBreaker(retries);
    }

    @Override
    public Mono<Profile> doGet(String url) {
        return Mono.fromCallable(() ->
                circuitBreaker.exec(() ->
                                webClient()
                                        .get()
                                        .uri(url)
                                        .retrieve()
                                        .bodyToMono(Profile.class)
                                        .doOnError(e ->
                                                log.error("API GET error: {}", e.getMessage())
                                        )
                                        .block(),
                        new Profile()
                )
        );
    }

    @Override
    public Mono<Object> doPost(String url, Profile profile) {
        return Mono.fromCallable(() ->
                circuitBreaker.exec(() ->
                                webClient()
                                        .post()
                                        .uri(url)
                                        .bodyValue(profile)
                                        .retrieve()
                                        .bodyToMono(Object.class)
                                        .doOnError(e ->
                                                log.error("API POST error: {}", e.getMessage())
                                        )
                                        .block(),
                        new Object()
                )
        );
    }

    @Override
    public Mono<Object> doPost(String url) {
        return Mono.fromCallable(() ->
                circuitBreaker.exec(() ->
                                webClient()
                                        .post()
                                        .uri(url)
                                        .retrieve()
                                        .bodyToMono(Object.class)
                                        .doOnError(e ->
                                                log.error("API POST error: {}", e.getMessage())
                                        )
                                        .block(),
                        new Object()
                )
        );
    }

    private WebClient webClient() {
        return webClient != null
                ? webClient
                : WebClient.create(urlServiceAuth);
    }
}
