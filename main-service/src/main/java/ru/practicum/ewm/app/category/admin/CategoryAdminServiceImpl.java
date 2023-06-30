package ru.practicum.ewm.app.category.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.app.category.CategoryRepository;
import ru.practicum.ewm.app.dto.category.CategoryDto;
import ru.practicum.ewm.app.category.model.Category;
import ru.practicum.ewm.app.category.model.CategoryDtoMapper;
import ru.practicum.ewm.common.exception.ConditionViolationException;
import ru.practicum.ewm.common.exception.NotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryAdminServiceImpl implements CategoryAdminService {

    private final CategoryRepository categoryRepo;

    private final CategoryDtoMapper categoryMapper;

    @Transactional
    @Override
    public CategoryDto addCategory(CategoryDto dto) {
        Category category = saveOrThrowConflictException(categoryMapper.fromDto(dto));
        log.info("admin:added new category added :[name:{}; id:{}]", category.getName(), category.getId());
        return categoryMapper.toDto(category);
    }

    @Transactional
    @Override
    public CategoryDto updateCategory(Long id, CategoryDto dto) {
        Category category = categoryRepo.getCategoryOrThrowNotFound(id);
        categoryMapper.update(dto, category);
        category = saveOrThrowConflictException(category);
        return categoryMapper.toDto(category);
    }

    @Transactional
    @Override
    public void deleteCategory(Long id) {
        try {
            log.info("deleting category by id {}", id);
            categoryRepo.deleteById(id);
        } catch (DataIntegrityViolationException dive) {
            throw new NotFoundException(String.format("Category with id=%d was not found", id));
        }
    }

    private Category saveOrThrowConflictException(Category category) {
        try {
            return categoryRepo.save(category);
        } catch (DataIntegrityViolationException dive) {
            throw new ConditionViolationException("category name clashes with present other category name");
        }
    }
}