package ru.practicum.ewm.app.subscription.model;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.ewm.app.dto.subscription.SubscriptionOutputDto;

/**
 * Mapstruct-Маппер интерфейс DTO  <--> подписка (Subscription)
 */
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = "spring")
public interface SubscriptionDtoMapper {

    SubscriptionOutputDto toDto(Subscription subscription);
}