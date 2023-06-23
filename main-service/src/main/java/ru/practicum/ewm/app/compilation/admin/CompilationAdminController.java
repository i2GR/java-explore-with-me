package ru.practicum.ewm.app.compilation.admin;

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

import ru.practicum.ewm.app.dto.CompilationOutputDto;
import ru.practicum.ewm.app.dto.CompilationInputDto;
import ru.practicum.ewm.common.validation.OnCreate;
import ru.practicum.ewm.common.validation.OnUpdate;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/compilations")
@Validated
public class CompilationAdminController {

    private final CompilationAdminService compilationAdminService;

    @PostMapping
    public ResponseEntity<CompilationOutputDto> addCompilation(
            @RequestBody @Validated(value = OnCreate.class) CompilationInputDto dto) {
        log.info("Admin API:post compilation");
        return new ResponseEntity<>(compilationAdminService.addCompilation(dto), HttpStatus.CREATED);
    }

    @PatchMapping("/{compId}")
    public ResponseEntity<CompilationOutputDto> updateCompilation(
            @PathVariable(name = "compId") Long compId,
            @RequestBody @Validated(value = OnUpdate.class) CompilationInputDto dto) {
        log.info("Admin API:patch compilation of id {}", compId);
        return new ResponseEntity<>(compilationAdminService.updateCompilation(compId, dto), HttpStatus.OK);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable(name = "compId") Long compId) {
        log.info("Admin API: delete compilation with id {}", compId);
        compilationAdminService.deleteCompilation(compId);
    }
}