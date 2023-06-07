package ru.practicum.ewm.app.compilation.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.app.compilation.CompilationRepository;
import ru.practicum.ewm.app.dto.CompilationOutputDto;
import ru.practicum.ewm.app.compilation.model.Compilation;
import ru.practicum.ewm.app.compilation.model.CompilationDtoMapper;
import ru.practicum.ewm.app.dto.CompilationInputDto;
import ru.practicum.ewm.app.event.EventRepository;
import ru.practicum.ewm.app.event.model.Event;
import ru.practicum.ewm.common.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationAdminServiceImpl implements CompilationAdminService {

    private final CompilationRepository compilationRepo;

    private final EventRepository eventRepo;

    private final CompilationDtoMapper compilationMapper;

    @Transactional
    @Override
    public CompilationOutputDto addCompilation(CompilationInputDto dto) {
        log.info("Admin Service: adding Compilation title {}", dto.getTitle());
        List<Event> eventList = dto.getEvents() == null
                ? List.of()
                : eventRepo.findAllById(dto.getEvents());
        Compilation created = compilationRepo.save(compilationMapper.fromInputDto(dto, eventList));
        log.info("Admin Service: new compilation added:[title:{}; id:{}]", created.getTitle(), created.getId());
        return compilationMapper.toOutputDto(created);
    }

    @Transactional
    @Override
    public CompilationOutputDto updateCompilation(Long id, CompilationInputDto dto) {
        log.info("Admin Service: updating Compilation id {} title {}", id, dto.getTitle());
        Compilation compilation =compilationRepo.findById(id)
                .orElseThrow(() -> new NotFoundException(format("Compilation with id=%d was not found", id)));
        List<Event> eventList = dto.getEvents() == null
                ? new ArrayList<>(compilation.getEvents())
                : eventRepo.findAllById(dto.getEvents());
        compilationMapper.update(dto, eventList, compilation);
        return compilationMapper.toOutputDto(compilation);
    }

    @Transactional
    @Override
    public void deleteCompilation(Long id) {
        try {
            log.info("deleting compilation by id {}", id);
            compilationRepo.deleteById(id);
        } catch (EmptyResultDataAccessException exception) {
            log.info("Compilation with id={} not found: throwing exception", id);
            throw new NotFoundException(String.format("Compilation with id=%d was not found", id));
        }
    }
}