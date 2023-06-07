package ru.practicum.ewm.app.compilation.publish;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.app.compilation.CompilationRepository;
import ru.practicum.ewm.app.compilation.model.CompilationDtoMapper;
import ru.practicum.ewm.app.dto.CompilationOutputDto;
import ru.practicum.ewm.app.event.EventRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationPublicServiceImpl implements CompilationPublicService {

    private final CompilationRepository compilationRepo;

    private final EventRepository eventRepo;

    private final CompilationDtoMapper compilationMapper;

    @Override
    public CompilationOutputDto getCompilationById(Long id) {
        log.info("Public: get compilation by Id {}", id);
        return compilationMapper.toOutputDto(compilationRepo.getCompilationOrThrowNotFound(id));
    }

    @Override
    public List<CompilationOutputDto> getCompilationListByConditions(Boolean pinned, Long from, Integer size) {
        log.info("Public: getting compilations from {} with size {} with pinned {}", from, size, pinned);
        PageRequest page = PageRequest.of((int) (from/size), size);
        List<Boolean> pinnedFilter = pinned == null ? List.of(true, false) : List.of(pinned);
        return compilationRepo.findAllByPinnedIn(pinnedFilter, page).stream()
                .map(compilationMapper::toOutputDto)
                .collect(Collectors.toList());
    }

}
