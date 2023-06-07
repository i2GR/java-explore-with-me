package ru.practicum.ewm.app.partrequest.privatized;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static java.lang.String.format;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.ewm.common.exception.BadRequestException;
import ru.practicum.ewm.common.exception.ConditionViolationException;
import ru.practicum.ewm.common.exception.NotFoundException;
import ru.practicum.ewm.common.utils.PartRequestStatus;
import ru.practicum.ewm.common.utils.EventState;
import ru.practicum.ewm.app.dto.PartRequestStatusUpdateRequest;
import ru.practicum.ewm.app.dto.PartRequestStatusUpdateResult;
import ru.practicum.ewm.app.dto.PartRequestDto;
import ru.practicum.ewm.app.event.EventRepository;
import ru.practicum.ewm.app.event.model.Event;
import ru.practicum.ewm.app.partrequest.PartRequestRepository;
import ru.practicum.ewm.app.partrequest.model.PartRequest;
import ru.practicum.ewm.app.partrequest.model.PartRequestDtoMapper;
import ru.practicum.ewm.app.user.UserRepository;
import ru.practicum.ewm.app.user.model.User;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PartRequestPrivateServiceImpl implements PartRequestPrivateService {

    private final PartRequestRepository requestRepo;

    private final UserRepository userRepo;

    private final EventRepository eventRepo;

    private final PartRequestDtoMapper mapper;

    @Transactional
    @Override
    public PartRequestDto addPartRequest(Long userId, Long eventId) {
        log.info("Private Service: adding new PartRequest from user id {} for event id{}", userId, eventId);
        checkDuplicatedRequest(userId, eventId);
        User participant = userRepo.getUserOrThrowNotFound(userId);
        Event event = eventRepo.getEventOrThrowNotFound(eventId);
        checkParticipantIsNotEventCreator(participant, event.getInitiator());
        checkEventPublication(event);
        PartRequestStatus status = getPartRequestStatus(event);
        PartRequest request = PartRequest.builder()
                .requester(participant)
                .event(event)
                .owner(event.getInitiator())
                .status(status)
                .created(LocalDateTime.now())
                .build();
        return mapper.toDto(requestRepo.save(request));
    }

    @Override
    public List<PartRequestDto> getPartRequestListByUser(Long userId) {
        log.info("Private Service: getting PartRequests from user id {}", userId);
        //реализация метода existsById() JPA репозитория использует вызов метода findById().isPresent()
        userRepo.getUserOrThrowNotFound(userId);
        return requestRepo.findByRequesterId(userId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public PartRequestDto cancelPartRequest(Long userId, Long requestId) {
        log.info("Private Service: cancelling PartRequest id {} from user id {}", requestRepo, userId);
        User participant = userRepo.getUserOrThrowNotFound(userId);
        PartRequest request = requestRepo.getPartRequestOrThrowNotFound(requestId);
        checkParticipantOfRequest(participant, request);
        request.setStatus(PartRequestStatus.CANCELED);
        return mapper.toDto(request);
    }

    @Override
    public List<PartRequestDto> getAllPartRequestsByEventIdFromUserId(Long userId, Long eventId) {
        log.info("Private Service: getting PartRequests for event id {} from user id {}", eventId, userId);
        return requestRepo.findAllByOwnerIdAndEventId(userId, eventId)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public PartRequestStatusUpdateResult updateRequest(Long userId, Long eventId, PartRequestStatusUpdateRequest dto) {
        log.info("Private Service: updating PartRequests for event id {} from user id {}", eventId, userId);
        PartRequestStatus newStatus = checkNewPartRequestStatus(dto.getStatus());
        List<Long> ids = dto.getRequestIds();
        Event event = eventRepo.getEventOrThrowNotFound(eventId);
        Map<Long, PartRequest> requests = requestRepo.findAllById(ids)
                .stream()
                .collect(Collectors.toMap(PartRequest::getId, pr -> pr));
        checkPendingRequests(requests);
        long freeParticipantCount = getAvailableParticipants(event, requests);
        return preparePartRequestStatusUpdateResult(ids, requests, freeParticipantCount, newStatus);
    }

    private void checkDuplicatedRequest(Long userId, Long eventId) {
        if (requestRepo.findAllByOwnerIdAndEventId(userId, eventId).size() > 0) {
            throw new ConditionViolationException("forbidden to duplicate requests for event from one user");
        }
    }

    private void checkParticipantIsNotEventCreator(User participant, User initiator) {
        if (participant.getId().equals(initiator.getId())) {
            throw new ConditionViolationException("event initiator cannot be requester for owned event");
        }
    }

    private void checkEventPublication(Event event) {
        if (event.getState() != EventState.PUBLISHED) {
            throw new ConditionViolationException("forbidden: event is not published");
        }
    }

    private PartRequestStatus getPartRequestStatus(Event event) {
        long limit = event.getParticipantLimit();
        if (limit == 0) {
            return PartRequestStatus.CONFIRMED;
        }
        long confirmedRequests = requestRepo.countByEventIdAndStatus(event.getId(), PartRequestStatus.CONFIRMED);
        if (confirmedRequests == limit) {
            throw new ConditionViolationException("The participant limit has been reached");
        }
        if (!event.getRequestModeration()) {
            return PartRequestStatus.CONFIRMED;
        }
        return PartRequestStatus.PENDING;
    }

    private long getAvailableParticipants(Event event, Map<Long, PartRequest> requests) {
        long limit = event.getParticipantLimit();
        if (!event.getRequestModeration() || limit == 0) {
            return requests.size();
        }
        return checkAvailabilityWithParticipationLimit(event, limit);
    }

    private long checkAvailabilityWithParticipationLimit(Event event, long limit) {
        long confirmedRequests = requestRepo.countByEventIdAndStatus(event.getId(), PartRequestStatus.CONFIRMED);
        if (confirmedRequests == limit) {
            throw new ConditionViolationException("The participant limit has been reached");
        }
        return limit - confirmedRequests;
    }

    private void checkParticipantOfRequest(User participant, PartRequest request) {
        if (!participant.getId().equals(request.getRequester().getId())) {
            throw new ConditionViolationException("пользователь не является автором запроса на участие");
        }
    }

    private void checkPendingRequests(Map<Long, PartRequest> requests) {
        if (requests.entrySet()
                .stream().anyMatch(lpp -> lpp.getValue().getStatus() != PartRequestStatus.PENDING)) {
            throw new ConditionViolationException("one or more request has not pending status");
        }
    }

    private PartRequestStatus checkNewPartRequestStatus(String str) {
        if (str == null) {
            throw new BadRequestException("Null value for participation request status");
        }
        try {
            PartRequestStatus newStatus = PartRequestStatus.valueOf(PartRequestStatus.class, str.toUpperCase());
            if (newStatus == PartRequestStatus.CONFIRMED || newStatus == PartRequestStatus.REJECTED) {
                return newStatus;
            }
            throw new ConditionViolationException ("New request update must have status CONFIRMED or REJECTED");
        } catch (IllegalArgumentException iae) {
            throw new BadRequestException("Bad value for participation request status");
        }
    }

    private PartRequestStatusUpdateResult preparePartRequestStatusUpdateResult(List<Long> ids,
                                                 Map<Long, PartRequest> requests,
                                                 long freeParticipantCount,
                                                 PartRequestStatus newStatus) {
        List<PartRequestDto> rejected = new ArrayList<>();
        List<PartRequestDto> confirmed = new ArrayList<>();
        for (Long id : ids) {
            PartRequest request = requests.get(id);
            if (request == null) {
                throw new NotFoundException(format("Part Request with id=%d was not found", id));
            }
            if (newStatus == PartRequestStatus.REJECTED) {
                saveNewPartRequestStatus(request, newStatus, rejected);
            }
            if (newStatus == PartRequestStatus.CONFIRMED) {
                if (freeParticipantCount > 0) {
                    saveNewPartRequestStatus(request, newStatus, confirmed);
                    freeParticipantCount--;
                } else {
                    saveNewPartRequestStatus(request, PartRequestStatus.REJECTED, rejected);
                }
            }

        }
        return PartRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmed)
                .rejectedRequests(rejected)
                .build();
    }

    private void saveNewPartRequestStatus(PartRequest request, PartRequestStatus newStatus, List<PartRequestDto> dtoList){
        request.setStatus(newStatus);
        dtoList.add(mapper.toDto(request));
    }
}