package ru.practicum.ewm.app.event.model;

import java.time.LocalDateTime;

import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import ru.practicum.ewm.app.dto.event.EventOutputShortDtoByFollower;
import ru.practicum.ewm.common.utils.EventState;
import ru.practicum.ewm.common.utils.EventStateAction;

import ru.practicum.ewm.app.dto.event.EventInputDto;
import ru.practicum.ewm.app.dto.event.EventOutputFullDto;
import ru.practicum.ewm.app.dto.event.EventOutputShortDto;
import ru.practicum.ewm.app.category.model.Category;
import ru.practicum.ewm.app.category.model.CategoryDtoMapper;
import ru.practicum.ewm.app.user.model.User;
import ru.practicum.ewm.app.user.model.UserDtoMapper;

/**
 * Mapstruct-маппер интерфейс DTO  <--> Событие (Event)
 */
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = "spring",
        uses = {UserDtoMapper.class, CategoryDtoMapper.class})
public interface EventDtoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "locationLat", source = "dto.location.lat")
    @Mapping(target = "locationLon", source = "dto.location.lon")
    @Mapping(target = "initiator", source = "initiator")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "state", expression = "java(getNewState(dto.getStateAction()))")
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "compilations", ignore = true)
    @Mapping(target = "eventDate", source = "eventDate")
    Event fromInputDto(EventInputDto dto, User initiator, Category category, LocalDateTime eventDate);

    @Mapping(target = "id", source = "event.id")
    @Mapping(target = "location.lat", source = "event.locationLat")
    @Mapping(target = "location.lon", source = "event.locationLon")
    EventOutputFullDto toFullDto(Event event);

    EventOutputShortDto toShortDto(Event event);

    EventOutputShortDtoByFollower toDtoForFollower(Event event, boolean newEvent, Long subscriptionId);

    @InheritConfiguration
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "locationLat", expression =  "java(updateCoordinate(dto.getLocation(), event.getLocationLat(), \"lat\"))")
    @Mapping(target = "locationLon", expression =  "java(updateCoordinate(dto.getLocation(), event.getLocationLon(), \"lon\"))")
    @Mapping(target = "state", expression = "java(notNullSource(getNewState(dto.getStateAction()), event.getState()))")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "compilations", ignore = true)
    @Mapping(target = "eventDate", source = "eventDate")
    void update(EventInputDto dto, Category category, @MappingTarget Event event, LocalDateTime eventDate);

    default <T> T notNullSource(T source, T target) {
        return source != null ? source : target;
    }

    default EventState getNewState(EventStateAction stateAction) {
        if (stateAction == null) return EventState.PENDING;
        return stateAction.getResult();
    }

    default Float updateCoordinate(EventInputDto.Location location, Float coordinate, String name) {
        if (location == null) return coordinate;
        if (name.equals("lon")) return location.getLon() == null ? coordinate : location.getLon();
        if (name.equals("lat")) return location.getLat() == null ? coordinate : location.getLat();
        return coordinate;
    }
}