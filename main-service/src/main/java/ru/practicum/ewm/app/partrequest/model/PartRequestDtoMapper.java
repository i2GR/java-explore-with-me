package ru.practicum.ewm.app.partrequest.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import ru.practicum.ewm.app.dto.PartRequestDto;

/**
 * Mapstruct-маппер интерфейс DTO  <--> запрос на участие в событии (PartRequest)
 */
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = "spring")
public interface PartRequestDtoMapper {

    @Mapping(target = "id", source = "request.id")
    @Mapping(target = "event", source = "event.id")
    @Mapping(target = "requester", source = "requester.id")
    PartRequestDto toDto(PartRequest request);
}