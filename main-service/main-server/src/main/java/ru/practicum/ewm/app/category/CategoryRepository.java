package ru.practicum.ewm.app.category;

import static java.lang.String.format;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.app.category.model.Category;
import ru.practicum.ewm.common.exception.NotFoundException;

public interface CategoryRepository extends JpaRepository<Category, Long>{

    default Category getCategoryOrThrowNotFound(Long id) {
        return findById(id).orElseThrow(
                () -> new NotFoundException(format("Category with id=%d was not found", id)));
    }
}