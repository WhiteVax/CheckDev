package ru.job4j.site.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import ru.job4j.site.exeption.IdNotFoundException;

import java.io.IOException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class RestTemplateResponseErrorHandlerTest {

    private RestTemplateResponseErrorHandler handler;
    private ClientHttpResponse response;

    @BeforeEach
    void init() {
        handler = new RestTemplateResponseErrorHandler();
        response = mock(ClientHttpResponse.class);
    }

    @Test
    void whenStatusIs404ThenHasErrorReturnTrue() throws IOException {
        when(response.getStatusCode())
                .thenReturn(HttpStatus.NOT_FOUND);
        boolean result = handler.hasError(response);
        assertThat(result).isTrue();
    }

    @Test
    void whenStatusIs500ThenHasErrorReturnTrue() throws IOException {
        when(response.getStatusCode())
                .thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
        boolean result = handler.hasError(response);
        assertThat(result).isTrue();
    }

    @Test
    void whenStatusIs200ThenHasErrorReturnFalse() throws IOException {
        when(response.getStatusCode()).thenReturn(HttpStatus.OK);
        boolean result = handler.hasError(response);
        assertThat(result).isFalse();
    }

    @Test
    void whenStatusIs404ThenThrowIdNotFoundException() throws IOException {
        when(response.getStatusCode())
                .thenReturn(HttpStatus.NOT_FOUND);
        assertThatThrownBy(() -> handler.handleError(response))
                .isInstanceOf(IdNotFoundException.class)
                .hasMessage("Пользователь не найден");
    }

    @Test
    void whenStatusIs500ThenThrowIdNotFoundException() throws IOException {
        when(response.getStatusCode())
                .thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThatThrownBy(() -> handler.handleError(response))
                .isInstanceOf(IdNotFoundException.class)
                .hasMessage("ID не найден");
    }

    @Test
    void whenStatusIs400ThenExceptionNotThrown() throws IOException {
        when(response.getStatusCode())
                .thenReturn(HttpStatus.BAD_REQUEST);
        assertThatCode(() -> handler.handleError(response))
                .doesNotThrowAnyException();
    }
}