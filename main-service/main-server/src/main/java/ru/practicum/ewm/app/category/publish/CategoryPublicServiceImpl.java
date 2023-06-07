package ru.practicum.ewm.app.category.publish;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.app.category.CategoryRepository;
import ru.practicum.ewm.app.category.model.CategoryDtoMapper;
import ru.practicum.ewm.app.dto.CategoryDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryPublicServiceImpl implements CategoryPublicService {

    private final CategoryRepository categoryRepo;

    private final CategoryDtoMapper categoryMapper;

    @Override
    public CategoryDto getCategory(Long id) {
        log.info("Public: get Category id {}", id);
        return categoryMapper.toDto(categoryRepo.getCategoryOrThrowNotFound(id));
    }

    @Override
    public List<CategoryDto> getCategoryListByConditions(Long from, Integer size) {
        log.info("Public: get Categories from {} with size {}", from, size);
        PageRequest page = PageRequest.of((int) (from/size), size);
        return categoryRepo.findAll(page).stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }
}