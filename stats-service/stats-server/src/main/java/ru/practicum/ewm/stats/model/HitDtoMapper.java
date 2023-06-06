package ru.practicum.ewm.stats.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.dto.EndpointHitResponseDto;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = "spring")
public interface HitDtoMapper {

    Stats fromDto(EndpointHitDto dto);

    @Mapping(target = "recorded", expression = "java(true)")
    EndpointHitResponseDto toDto(Stats stats);
}