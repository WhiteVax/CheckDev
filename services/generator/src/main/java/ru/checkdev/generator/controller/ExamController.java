package ru.checkdev.generator.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.checkdev.generator.domain.Exam;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/exam")
@RequiredArgsConstructor
public class ExamController {

    @GetMapping("/create/")
    public ResponseEntity<Exam> get(@RequestParam("url") String text) throws UnsupportedEncodingException {

        text = URLDecoder.decode(text, "UTF-8");
        Set<String> set = new HashSet<>();
        set.add("1 GENERATED STRING FROM LINK: " + text);
        set.add("2 GENERATED STRING FROM LINK: " + text);
        set.add("3 GENERATED STRING FROM LINK: " + text);
        var result = new Exam(set);
        return ResponseEntity.status(result.getQuestions().size() > 0 ? HttpStatus.OK : HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON).body(result);
    }
}
