package ru.checkdev.notification.telegram.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class RetryTemplate {
    private final int retries;
    private final long delay;

    @FunctionalInterface
    public interface Act<T> {
        T exec() throws Exception;
    }

    public <R> R exec(Act<R> act, R defVal) {
        var attempt = 0;
        do {
            attempt++;
            try {
                return act.exec();
            } catch (Exception e) {
                log.error("Attempt {} failed: {}", attempt, e.getMessage(), e);
                sleep();
            }
        } while (attempt < retries);
        return defVal;
    }

    private void sleep() {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted during retry delay", e);
        }
    }
}