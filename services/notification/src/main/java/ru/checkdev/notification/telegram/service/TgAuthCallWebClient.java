package ru.checkdev.notification.telegram.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.checkdev.notification.domain.Profile;

/**
 * Класс реализует методы get и post для отправки сообщений через WebClient
 *
 * @author Dmitry Stepanov, user Dmitry
 * @since 12.09.2023
 */
@org.springframework.context.annotation.Profile("default")
@Service
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class TgAuthCallWebClient implements TgCall {
    private final static String NAME_RETRY = "tgAuthRetry";
    private final static String AUTH_CIRCUIT_BREAKER = "tgAuthCircuitBreaker";
    @Value("${server.auth}")
    private String urlServiceAuth;

    /**
     * Метод get
     *
     * @param url URL http
     * @return Mono<Person>
     */
    @Retry(name = NAME_RETRY)
    @CircuitBreaker(name = AUTH_CIRCUIT_BREAKER, fallbackMethod = "fallbackGet") // Применение Circuit Breaker
    @Override
    public Mono<Profile> doGet(String url) {
        return WebClient.create(urlServiceAuth)
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(Profile.class)
                .doOnError(err -> log.error("API not found: {}", err.getMessage()));
    }

    public Mono<Profile> fallbackGet(String url, Throwable throwable) {
        log.error(
                "Fallback GET triggered for url {}: {}",
                url,
                throwable.getMessage()
        );
        return Mono.empty();
    }

    /**
     * Метод POST
     *
     * @param url     URL http
     * @param profile Body PersonDTO.class
     * @return Mono<Person>
     */
    @Retry(name = NAME_RETRY)
    @CircuitBreaker(name = AUTH_CIRCUIT_BREAKER, fallbackMethod = "fallbackPost") // Применение Circuit Breaker
    @Override
    public Mono<Object> doPost(String url, Profile profile) {
        return WebClient.create(urlServiceAuth)
                .post()
                .uri(url)
                .bodyValue(profile)
                .retrieve()
                .bodyToMono(Object.class)
                .doOnError(err -> log.error("API not found: {}", err.getMessage()));
    }

    public Mono<Object> fallbackPost(String url, Profile profile, Throwable throwable) {
        log.error(
                "Fallback POST triggered for url {}: {}, username {}: {}",
                url,
                profile.getUsername(),
                throwable.getMessage()
        );
        return Mono.empty();
    }

    @Retry(name = NAME_RETRY)
    @CircuitBreaker(name = AUTH_CIRCUIT_BREAKER, fallbackMethod = "fallbackPostUrl") // Применение Circuit Breaker
    @Override
    public Mono<Object> doPost(String url) {
        return WebClient.create(urlServiceAuth)
                .post()
                .uri(url)
                .retrieve()
                .bodyToMono(Object.class)
                .doOnError(err -> log.error("API not found: {}", err.getMessage()));
    }

    public Mono<Object> fallbackPostUrl(String url, Throwable throwable) {
        log.error(
                "Fallback POST triggered for url {}: {}",
                url,
                throwable.getMessage()
        );
        return Mono.empty();
    }
}
