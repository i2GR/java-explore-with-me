package ru.practicum.ewm.app.partrequest;

import java.util.List;
import static java.lang.String.format;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.practicum.ewm.common.exception.NotFoundException;
import ru.practicum.ewm.common.utils.PartRequestStatus;
import ru.practicum.ewm.app.event.model.Event;
import ru.practicum.ewm.app.partrequest.model.PartRequest;
import ru.practicum.ewm.app.partrequest.model.ConfirmedRequestCount;

public interface PartRequestRepository extends JpaRepository<PartRequest, Long> {

    Long countByEventIdAndStatus(Long eventId, PartRequestStatus status);

    List<PartRequest> findByRequesterId(Long userId);

    @Query(value = "SELECT new ru.practicum.ewm.app.partrequest.model.ConfirmedRequestCount(r.event, COUNT(r.status)) "
            + "FROM PartRequest AS r "
            + "WHERE r.event IN (:events) AND r.status = ru.practicum.ewm.common.utils.PartRequestStatus.CONFIRMED "
            + "GROUP BY r.event "
            + "ORDER BY COUNT(r.status) DESC")
    List<ConfirmedRequestCount> getConfirmedRequestCount(@Param("events") List<Event> events);

    List<PartRequest> findAllByOwnerIdAndEventId(Long initiatorId, Long eventId);

    default PartRequest getPartRequestOrThrowNotFound(Long id) {
        return findById(id).orElseThrow(
                () -> new NotFoundException(format("Participation Request with id=%d was not found", id)));
    }
}