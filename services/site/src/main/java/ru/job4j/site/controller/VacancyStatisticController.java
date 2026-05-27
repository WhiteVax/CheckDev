package ru.job4j.site.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.site.dto.DirectionKey;
import ru.job4j.site.service.VacancyStatisticService;
import ru.job4j.site.util.RequestResponseTools;

import javax.servlet.http.HttpServletRequest;

import static ru.job4j.site.util.RequestResponseTools.getToken;

@Controller
@RequestMapping("/statistic")
@AllArgsConstructor
public class VacancyStatisticController {

    private final VacancyStatisticService service;

    @GetMapping("/createForm")
    public String createForm(Model model) throws JsonProcessingException {
        RequestResponseTools.addAttrBreadcrumbs(model,
                "Главная", "/index",
                "Создать ключ", "/statistic/createForm"
        );
        return "statistic/createForm";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute DirectionKey directionKey, HttpServletRequest req)
            throws JsonProcessingException {
        directionKey.setName(StringEscapeUtils.escapeHtml4(directionKey.getName()));
        service.create(getToken(req), directionKey);
        return "redirect:/index/";
    }

    @GetMapping("/editForm/{id}/{name}")
    public String editForm(@PathVariable("id") int id, @PathVariable("name") String name,
                           Model model) throws JsonProcessingException {
        model.addAttribute("directionKey", new DirectionKey(id, name));
        RequestResponseTools.addAttrBreadcrumbs(model,
                "Главная", "/index",
                "Редактировать ключ", "/statistic/updateForm"
        );
        return "statistic/editForm";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute DirectionKey directionKey,
                         HttpServletRequest req) throws JsonProcessingException {
        directionKey.setName(StringEscapeUtils.escapeHtml4(directionKey.getName()));
        service.update(getToken(req), directionKey);
        return "redirect:/index";
    }

    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable int id) throws JsonProcessingException {
        service.delete(id);
        return "/index";
    }
}
