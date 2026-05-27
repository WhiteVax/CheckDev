package ru.checkdev.generator.service.vacancy;

import org.springframework.stereotype.Service;
import ru.checkdev.generator.util.RestCall;

@Service
public class HeadHunterVacancyService implements VacancyService {

    private static final String URL = "https://api.hh.ru/vacancies/";

    @Override
    public String getDescription(String token, String vacancyId) {
        return new RestCall(String.format("%s%s", URL, vacancyId)).get(token);
    }
}
