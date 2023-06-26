package ru.practicum.ewm.app.subscription;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.app.subscription.model.Subscription;
import ru.practicum.ewm.common.exception.NotFoundException;

import java.util.List;

import static java.lang.String.format;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    List<Subscription> findAllByFollowerId(Long followerId, Pageable page);

    void deleteByFollowerIdAndId(Long followerId, Long subscriptionId);

    default Subscription getSubscriptionOrThrowNotFound(Long id) {
        return findById(id).orElseThrow(
                () -> new NotFoundException(format("Subscription with id=%d was not found", id)));
    }
}
