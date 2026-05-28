package ru.checkdev.notification.telegram.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.checkdev.notification.domain.Profile;

import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Testing TgAuthCallWebClint
 *
 * @author Dmitry Stepanov, user Dmitry
 * @since 06.10.2023
 */
@ExtendWith(MockitoExtension.class)
class TgAuthCallWebClientTest {
    private static final String URL = "http://tetsurl:15000";
    @Mock
    private WebClient webClientMock;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersMock;
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriMock;
    @Mock
    private WebClient.RequestBodySpec requestBodyMock;
    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriMock;
    @Mock
    private WebClient.ResponseSpec responseMock;
    @Mock
    private TgAuthCallWebClient tgAuthCallWebClient;

    @Disabled
    @Test
    void whenDoGetThenReturnPersonDTO() {
        int personId = 100;
        Calendar created = new Calendar.Builder()
                .set(Calendar.DAY_OF_MONTH, 23)
                .set(Calendar.MONTH, Calendar.OCTOBER)
                .set(Calendar.YEAR, 2023)
                .build();
        Profile profile = new Profile(
                0, "username", "mail", "password", true, created);
        when(webClientMock.get()).thenReturn(requestHeadersUriMock);
        when(requestHeadersUriMock.uri("/person/" + personId)).thenReturn(requestHeadersMock);
        when(requestHeadersMock.retrieve()).thenReturn(responseMock);
        when(responseMock.bodyToMono(Profile.class)).thenReturn(Mono.just(profile));

        Profile actual = tgAuthCallWebClient.doGet("/person/" + personId).block();

        assertThat(actual).isEqualTo(profile);
    }

    @Disabled
    @Test
    void whenDoGetThenReturnExceptionError() {
        int personId = 100;
        when(webClientMock.get()).thenReturn(requestHeadersUriMock);
        when(requestHeadersUriMock.uri("/person/" + personId)).thenReturn(requestHeadersMock);
        when(requestHeadersMock.retrieve()).thenReturn(responseMock);
        when(responseMock.bodyToMono(Profile.class)).thenReturn(Mono.error(new Throwable("Error")));

        assertThatThrownBy(() -> tgAuthCallWebClient.doGet("/person/" + personId).block())
                .isInstanceOf(Throwable.class)
                .hasMessageContaining("Error");
    }

    @Disabled
    @Test
    void whenDoPostSavePersonThenReturnNewPerson() {
        Calendar created = new Calendar.Builder()
                .set(Calendar.DAY_OF_MONTH, 23)
                .set(Calendar.MONTH, Calendar.OCTOBER)
                .set(Calendar.YEAR, 2023)
                .build();
        Profile profile = new Profile(
                0, "username", "mail", "password", true, created);
        when(webClientMock.post()).thenReturn(requestBodyUriMock);
        when(requestBodyUriMock.uri("/person/created")).thenReturn(requestBodyMock);
        when(requestBodyMock.bodyValue(profile)).thenReturn(requestHeadersMock);
        when(requestHeadersMock.retrieve()).thenReturn(responseMock);
        when(responseMock.bodyToMono(Object.class)).thenReturn(Mono.just(profile));
        Mono<Object> objectMono = tgAuthCallWebClient.doPost("/person/created", profile);

        Profile actual = (Profile) objectMono.block();

        assertThat(actual).isEqualTo(profile);
    }

    @Test
    void whenAuthGetFailsTemporarilyThenRetryReturnsProfile() {
        var attempts = new AtomicInteger();
        Calendar created = new Calendar.Builder()
                .set(Calendar.DAY_OF_MONTH, 28)
                .set(Calendar.MONTH, Calendar.MAY)
                .set(Calendar.YEAR, 2026)
                .build();

        Profile profile = new Profile(
                1,
                "username",
                "mail@test.com",
                "password",
                true,
                created
        );
        WebClient webClient = mock(WebClient.class);
        WebClient.RequestHeadersUriSpec uriSpec =
                mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec =
                mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec =
                mock(WebClient.ResponseSpec.class);
        when(webClient.get()).thenReturn((WebClient.RequestHeadersUriSpec) uriSpec);
        when(uriSpec.uri("/profiles/tg/1")).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Profile.class))
                .thenAnswer(invocation -> {
                    if (attempts.incrementAndGet() < 3) {
                        return Mono.error(new IllegalStateException("temporary error"));
                    }
                    return Mono.just(profile);
                });
        var client = new TgAuthCallWebClient(webClient, 3, 0);
        Profile result = client.doGet("/profiles/tg/1").block();
        assertThat(result).isEqualTo(profile);
        assertThat(attempts.get()).isEqualTo(3);

    }
}