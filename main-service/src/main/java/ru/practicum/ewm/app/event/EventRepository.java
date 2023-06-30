package ru.practicum.ewm.app.event;

import java.time.LocalDateTime;
import java.util.List;
import static java.lang.String.format;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.common.exception.NotFoundException;
import ru.practicum.ewm.common.utils.EventState;
import ru.practicum.ewm.app.event.model.Event;

public interface EventRepository extends JpaRepository<Event, Long> {

    /**
     * Admin API get with filters
     */
    List<Event> findAllByInitiatorIdInAndStateInAndCategoryIdInAndEventDateBetween(List<Long> ids,
                                                                                   List<EventState> states,
                                                                                   List<Long> categories,
                                                                                   LocalDateTime start,
                                                                                   LocalDateTime end,
                                                                                   Pageable pageable);

    List<Event> findAllByInitiatorIdInAndStateInAndEventDateBetween(List<Long> ids,
                                                                    List<EventState> states,
                                                                    LocalDateTime start,
                                                                    LocalDateTime end,
                                                                    Pageable pageable);

    List<Event> findAllByStateInAndEventDateBetween(List<EventState> states,
                                                    LocalDateTime start,
                                                    LocalDateTime end,
                                                    Pageable pageable);

    List<Event> findAllByStateInAndCategoryIdInAndEventDateBetween(List<EventState> states,
                                                                   List<Long> categories,
                                                                   LocalDateTime start,
                                                                   LocalDateTime end,
                                                                   Pageable pageable);

    /**
     * Private API get events for User
     */
    List<Event> findAllByInitiatorId(Long id, Pageable pageable);

    /**
     * Public API get events
     */
    List<Event> findByAnnotationOrDescriptionContainingIgnoreCaseAndCategoryIdInAndPaidInAndEventDateBetweenAndState(
            String queryInAnnotation,
            String queryInDescription,
            List<Long> categoryIds,
            List<Boolean> paid,
            LocalDateTime start,
            LocalDateTime end,
            EventState state,
            Pageable pageable);

    @Query(value = "SELECT e "
            + "FROM Event e "
            + "INNER JOIN User u ON u.id = e.initiator.id "
            + "INNER JOIN Subscription s ON s.leader.id = u.id "
            + "WHERE s.follower.id = :followerId AND e.state = 'PUBLISHED' "
            + "ORDER BY e.eventDate ASC")
    List<Event> getEventsByFollower(@Param("followerId") Long followerId, Pageable page);

    default Event getEventOrThrowNotFound(Long id) {
        return findById(id).orElseThrow(
                () -> new NotFoundException(format("Event with id=%d was not found", id)));
    }
}