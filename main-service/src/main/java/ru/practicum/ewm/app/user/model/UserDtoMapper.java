package ru.practicum.ewm.app.user.model;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.ewm.app.dto.user.UserCommonFullDto;
import ru.practicum.ewm.app.dto.user.UserOutputShortDto;

/**
 * Mapstruct-Маппер интерфейс DTO  <--> Пользователь (User)
 */
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = "spring")
public interface UserDtoMapper {

    User fromFullDto(UserCommonFullDto dto);

    UserCommonFullDto toFullDto(User user);

    UserOutputShortDto toShortDto(User user);
}