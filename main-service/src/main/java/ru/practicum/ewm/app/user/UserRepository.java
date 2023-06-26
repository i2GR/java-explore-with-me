package ru.practicum.ewm.app.user;

import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.app.user.model.SubscriptionCountedUser;
import ru.practicum.ewm.app.user.model.User;
import ru.practicum.ewm.common.exception.NotFoundException;

public interface UserRepository extends JpaRepository<User,Long> {

    List<User> findAllBy(Pageable pageable);

    List<User> findAllByIdIn(List<Long> ids, Pageable pageable);


    @Query(value = "SELECT new ru.practicum.ewm.app.user.model.SubscriptionCountedUser(u, COUNT(s.leader.id)) "
            + "FROM User AS u "
            + "LEFT JOIN Subscription AS s ON s.leader.id = u.id "
            + "WHERE s.leader.id = :userId "
            + "GROUP BY u.id "
            + "ORDER BY COUNT(s.leader.id) DESC")
    Optional<SubscriptionCountedUser> getUserWithSubscriptionCount(@Param("userId") Long userId);

    @Query(value = "SELECT new ru.practicum.ewm.app.user.model.SubscriptionCountedUser(u, COUNT(s.leader.id)) "
            + "FROM User AS u  "
            + "INNER JOIN Subscription AS s ON s.leader.id = u.id "
            + "GROUP BY u.id "
            + "ORDER BY COUNT(s.leader.id) DESC")
    List<SubscriptionCountedUser> getAllUsersWithSubscriptionCount(Pageable page);

    @Query(value = "SELECT new ru.practicum.ewm.app.user.model.SubscriptionCountedUser(u, COUNT(s.leader.id)) "
            + "FROM User AS u  "
            + "INNER JOIN Subscription AS s ON s.leader.id = u.id "
            + "WHERE s.leader.id IN (:userIds) "
            + "GROUP BY u.id ")
    List<SubscriptionCountedUser> getAllUsersByIdsWithSubscriptionCount(@Param("userIds") List<Long> userIds, Pageable page);

    default User getUserOrThrowNotFound(Long id) {
        return findById(id).orElseThrow(
                () -> new NotFoundException(format("User with id=%d was not found", id)));
    }

    default SubscriptionCountedUser getUserWithSubscriptionsCountOrThrowNotFound(Long id) {
        return getUserWithSubscriptionCount(id).orElseThrow(
                () -> new NotFoundException(format("User with id=%d was not found", id)));
    }
}