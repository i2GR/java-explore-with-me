package ru.practicum.ewm.app.compilation.publish;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.app.dto.CompilationOutputDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/compilations")
@Validated
public class CompilationPublicController {
    private final CompilationPublicService service;

    @GetMapping("/{compId}")
    public ResponseEntity<CompilationOutputDto> getCompilation(
            @PathVariable(name = "compId") @Positive Long compId) {
        log.info("Public API: get compilation id {}", compId);
        return new ResponseEntity<>(service.getCompilationById(compId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<CompilationOutputDto>> getCompilations(
            @RequestParam(name = "pinned", required = false) Boolean pinned,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Long from,
            @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {
        log.info("Public API: get compilations from {} of size {}", from, size);
        return new ResponseEntity<>(service.getCompilationListByConditions(pinned, from, size), HttpStatus.OK);
    }
}