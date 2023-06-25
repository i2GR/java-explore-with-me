package ru.practicum.ewm.app.compilation;

import java.util.List;
import static java.lang.String.format;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import ru.practicum.ewm.app.compilation.model.Compilation;
import ru.practicum.ewm.common.exception.NotFoundException;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    List<Compilation> findAllByPinnedIn(List<Boolean> pinned, Pageable pageable);

    default Compilation getCompilationOrThrowNotFound(Long id) {
        return findById(id).orElseThrow(
                () -> new NotFoundException(format("Compilation with id=%d was not found", id)));
    }
}