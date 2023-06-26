package ru.practicum.ewm.app.category.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

import javax.validation.constraints.Positive;

import ru.practicum.ewm.app.dto.category.CategoryDto;
import ru.practicum.ewm.common.validation.OnCreate;
import ru.practicum.ewm.common.validation.OnUpdate;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/categories")
@Validated
public class CategoryAdminController {

    private final CategoryAdminService service;

    @PostMapping
    public ResponseEntity<CategoryDto> addCategory(@RequestBody @Validated(value = OnCreate.class) CategoryDto dto) {
        log.info("Admin API:posting category");
        return new ResponseEntity<>(service.addCategory(dto), HttpStatus.CREATED);
    }

    @PatchMapping("/{catId}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable(name = "catId") @Positive Long catId,
                                                      @RequestBody @Validated(value = OnUpdate.class) CategoryDto dto) {
        log.info("Admin API:patching category with id {}", catId);
        return new ResponseEntity<>(service.updateCategory(catId, dto), HttpStatus.OK);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable(name = "catId") Long catId) {
        log.info("Admin API: deleting category with id {}", catId);
        service.deleteCategory(catId);
    }
}