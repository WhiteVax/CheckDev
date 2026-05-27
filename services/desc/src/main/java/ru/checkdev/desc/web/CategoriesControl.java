package ru.checkdev.desc.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.checkdev.desc.dto.CategoryDTO;
import ru.checkdev.desc.service.CategoryService;

import java.util.List;

@Tag(name = "CategoriesControl", description = "Categories REST API")
@RequestMapping("/categories")
@RestController
@AllArgsConstructor
public class CategoriesControl {
    private final CategoryService categoryService;

    @GetMapping("/")
    public List<CategoryDTO> getAll() {
        return categoryService.getAllCategoryDTO();
    }

    @GetMapping("/most_pop")
    public List<CategoryDTO> getMostPopular() {
        return categoryService.getMostPopular();
    }
}
