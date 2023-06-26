package ru.practicum.ewm.app.compilation.model;

import java.util.List;

import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import ru.practicum.ewm.app.dto.compilation.CompilationOutputDto;
import ru.practicum.ewm.app.dto.compilation.CompilationInputDto;
import ru.practicum.ewm.app.event.model.Event;
import ru.practicum.ewm.app.event.model.EventDtoMapper;

/**
 * Mapstruct-Маппер интерфейс DTO  <--> Подборки событий (Compilation)
 */
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = "spring",
        uses = {EventDtoMapper.class})
public interface CompilationDtoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", source = "events")
    Compilation fromInputDto(CompilationInputDto dto, List<Event> events);

    @Mapping(target = "id", source = "compilation.id")
    CompilationOutputDto toOutputDto(Compilation compilation);


    @InheritConfiguration
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", source = "events")
    void update(CompilationInputDto dto, List<Event> events, @MappingTarget Compilation compilation);
}