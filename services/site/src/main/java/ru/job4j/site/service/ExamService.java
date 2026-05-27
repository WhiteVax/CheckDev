package ru.job4j.site.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.job4j.site.domain.Exam;
import ru.job4j.site.util.RestAuthCall;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExamService {

    private final EurekaUriProvider uriProvider;
    private static final String SERVICE_ID = "generator";
    private static final String DIRECT = "/exam/";

    public Exam create(String token, String vacancyLink)
            throws Exception {
        String encodedUrl = URLEncoder.encode(vacancyLink, StandardCharsets.UTF_8);
        var text = new RestAuthCall(String
                .format("%s%screate/?url=%s", uriProvider.getUri(SERVICE_ID), DIRECT, encodedUrl))
                .get(token);
        return new ObjectMapper().readValue(text, Exam.class);
    }
}
