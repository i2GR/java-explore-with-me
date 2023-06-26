package ru.practicum.ewm.app.category.publish;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.ewm.app.dto.category.CategoryDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/categories")
@Validated
public class CategoryPublicController {
    private final CategoryPublicService service;

    @GetMapping("/{catId}")
    public ResponseEntity<CategoryDto> getCategory(
            @PathVariable(name = "catId") @Positive Long catId) {
        log.info("Public API: get category id {}", catId);
        return new ResponseEntity<>(service.getCategory(catId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getCategories(
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Long from,
            @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {
        log.info("Public API: get categories from {} of size {}", from, size);
        return new ResponseEntity<>(service.getCategoryListByConditions(from, size), HttpStatus.OK);
    }
}