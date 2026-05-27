package ru.checkdev.generator.util.parser;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class HeadHunterParser implements Parser<Integer> {

    @Override
    public Integer parse(String uri) {
        String json = "";
        try {
            json = Jsoup.connect(uri).ignoreContentType(true).execute().body();
        } catch (IOException e) {
            log.error("при попытке получить данные по uri {}, произошла ошибка: {}", uri, e.getMessage());
        }
        JsonElement element = JsonParser.parseString(json);
        JsonObject object = element.getAsJsonObject();
        return object.get("found").getAsInt();
    }
}
