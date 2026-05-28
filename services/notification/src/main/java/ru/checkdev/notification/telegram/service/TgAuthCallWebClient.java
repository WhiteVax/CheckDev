package ru.checkdev.notification.telegram.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.checkdev.notification.domain.Profile;
import ru.checkdev.notification.telegram.config.RetryTemplate;

/**
 * Класс реализует методы get и post для отправки сообщений через WebClient
 *
 * @author Dmitry Stepanov, user Dmitry
 * @since 12.09.2023
 */
@org.springframework.context.annotation.Profile("default")
@Service
@Slf4j
public class TgAuthCallWebClient implements TgCall {

    @Value("${server.auth}")
    private String urlServiceAuth;

    @Value("${retry.retries:3}")
    private int retries;

    @Value("${retry.delay:1000}")
    private long delay;

    private WebClient webClient;

    public TgAuthCallWebClient() {
    }

    public TgAuthCallWebClient(String urlServiceAuth, int retries, long delay) {
        this.urlServiceAuth = urlServiceAuth;
        this.retries = retries;
        this.delay = delay;
    }

    public TgAuthCallWebClient(WebClient webClient, int retries, long delay) {
        this.webClient = webClient;
        this.retries = retries;
        this.delay = delay;
    }

    /**
     * Метод get
     *
     * @param url URL http
     * @return Mono<Profile>
     */
    @Override
    public Mono<Profile> doGet(String url) {
        return Mono.fromCallable(() ->
                retry().exec(() ->
                                webClient()
                                        .get()
                                        .uri(url)
                                        .retrieve()
                                        .bodyToMono(Profile.class)
                                        .doOnError(err ->
                                                log.error("API not found: {}", err.getMessage())
                                        )
                                        .block(),
                        new Profile()
                )
        );
    }

    /**
     * Метод POST
     *
     * @param url     URL http
     * @param profile Body Profile.class
     * @return Mono<Object>
     */
    @Override
    public Mono<Object> doPost(String url, Profile profile) {
        return Mono.fromCallable(() ->
                retry().exec(() ->
                                webClient()
                                        .post()
                                        .uri(url)
                                        .bodyValue(profile)
                                        .retrieve()
                                        .bodyToMono(Object.class)
                                        .doOnError(err ->
                                                log.error("API not found: {}", err.getMessage())
                                        )
                                        .block(),
                        new Object()
                )
        );
    }

    @Override
    public Mono<Object> doPost(String url) {
        return Mono.fromCallable(() ->
                retry().exec(() ->
                                webClient()
                                        .post()
                                        .uri(url)
                                        .retrieve()
                                        .bodyToMono(Object.class)
                                        .doOnError(err ->
                                                log.error("API not found: {}", err.getMessage())
                                        )
                                        .block(),
                        new Object()
                )
        );
    }

    private RetryTemplate retry() {
        return new RetryTemplate(retries, delay);
    }

    private WebClient webClient() {
        return webClient != null
                ? webClient
                : WebClient.create(urlServiceAuth);
    }
}
