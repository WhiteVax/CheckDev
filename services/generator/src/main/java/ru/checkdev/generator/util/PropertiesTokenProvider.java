package ru.checkdev.generator.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;

@Slf4j
public class PropertiesTokenProvider implements TokenProvider {

    @Override
    public String getToken(String key) {
        if (key == null) {
            return null;
        }
        String result = "";
        try {
            Resource resource = new ClassPathResource("application.properties");
            Properties properties = PropertiesLoaderUtils.loadProperties(resource);
            String value = properties.getProperty(key);
            if (value != null) {
                result = value;
            }
        } catch (IOException e) {
            log.error("При попытке чтения файла application.properties произошла ошибка: {}", e.getMessage());
        }
        return result;
    }
}
