package ru.practicum.ewm.app.category.model;

import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import ru.practicum.ewm.app.dto.CategoryDto;

/**
 * Mapstruct-маппер интерфейс DTO  <--> Категории событий (Compilation)
 */
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = "spring")
public interface CategoryDtoMapper {

    Category fromDto(CategoryDto dto);

    CategoryDto toDto(Category category);

    @InheritConfiguration
    void update(CategoryDto dto, @MappingTarget Category category);
}