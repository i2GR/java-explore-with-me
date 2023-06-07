package ru.practicum.ewm.app.user;

import java.util.List;
import static java.lang.String.format;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import ru.practicum.ewm.app.user.model.User;
import ru.practicum.ewm.common.exception.NotFoundException;

public interface UserRepository extends JpaRepository<User, Long>{

    List<User> findAllBy(Pageable pageable);

    List<User> findAllByIdIn(List<Long> ids, Pageable pageable);

    default User getUserOrThrowNotFound(Long id) {
        return findById(id).orElseThrow(
                () -> new NotFoundException(format("User with id=%d was not found", id)));
    }
}