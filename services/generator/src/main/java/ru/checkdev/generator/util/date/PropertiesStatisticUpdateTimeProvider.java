package ru.checkdev.generator.util.date;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalTime;
import java.util.Properties;

@Component
@Slf4j
public class PropertiesStatisticUpdateTimeProvider implements TimeProvider<LocalTime> {

    @Override
    public LocalTime getTime() {
        String scheduledDate = "";
        try {
            Resource resource = new ClassPathResource("application.properties");
            Properties properties = PropertiesLoaderUtils.loadProperties(resource);
            scheduledDate = properties.getProperty("scheduled.task.cron");
        } catch (IOException e) {
            log.error("При попытке чтения файла application.properties произошла ошибка: {}",
                    e.getMessage());
        }
        String[] parts = scheduledDate.split(" ");
        String hour = parts[2];
        String minute = parts[1];
        return LocalTime.of(Integer.parseInt(hour), Integer.parseInt(minute));
    }
}
