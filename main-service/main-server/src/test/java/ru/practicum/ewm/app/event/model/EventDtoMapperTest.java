package ru.practicum.ewm.app.event.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.ewm.app.category.model.Category;
import ru.practicum.ewm.app.dto.EventInputDto;
import ru.practicum.ewm.app.dto.EventOutputFullDto;
import ru.practicum.ewm.app.user.model.User;
import ru.practicum.ewm.common.utils.Constants;
import ru.practicum.ewm.common.utils.EventState;
import ru.practicum.ewm.common.utils.EventStateAction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.ewm.common.utils.Constants.MIN_TIME;

@SpringBootTest
class EventDtoMapperTest {

    @Autowired
    private EventDtoMapper mapper;

    private User initiator;
    private Category category;

    @BeforeEach
    void setup() {
        initiator = User.builder()
                .id(1L)
                .name("user")
                .email("mail@host.dom")
                .build();
        category = Category.builder()
                .id(1L)
                .name("category")
                .build();
    }

    @Test
    void fromInputDto() {
        //given
        EventInputDto inputDto = setupInputDto();
        //when
        //TODO TIME
        //Event event = mapper.fromInputDto(inputDto, initiator, category);
        Event event = mapper.fromInputDto(inputDto, initiator, category, MIN_TIME);
        //then
        assertEquals(true, event.getPaid());
        assertEquals(1F, event.getLocationLat());
        assertEquals(2F, event.getLocationLon());
        assertEquals(true, event.getRequestModeration());
        assertEquals("annotation", event.getAnnotation());
        assertEquals(MIN_TIME, event.getEventDate());
        assertEquals("description", event.getDescription());
        assertEquals("title", event.getTitle());
        assertEquals(EventState.PENDING, event.getState());
        assertEquals(category, event.getCategory());
        assertEquals(initiator, event.getInitiator());
    }

    @Test
    void toFullDto() {
        //given
        Event event = setupEvent();
        //when
        EventOutputFullDto eventDto = mapper.toFullDto(event);
        //then
        assertEquals(true, eventDto.getPaid());
        assertEquals(1F, eventDto.getLocation().getLat());
        assertEquals(2F, eventDto.getLocation().getLon());
        assertEquals(true, eventDto.getRequestModeration());
        assertEquals("annotation", eventDto.getAnnotation());
        assertEquals(MIN_TIME, eventDto.getEventDate());
        assertEquals(MIN_TIME, eventDto.getPublishedOn());
        assertEquals("description", eventDto.getDescription());
        assertEquals("title", eventDto.getTitle());
        assertEquals(EventState.PENDING, eventDto.getState());
        assertEquals(1L, eventDto.getCategory().getId());
        assertEquals("category", eventDto.getCategory().getName());
        assertEquals(1L, eventDto.getInitiator().getId());
        assertEquals("user", eventDto.getInitiator().getName());
        assertEquals(1L, eventDto.getViews());
    }

    @Test
    void toShortDto() {
        //given
        Event event = setupEvent();
        //when
        EventOutputFullDto eventDto = mapper.toFullDto(event);
        //then
        assertEquals(true, eventDto.getPaid());
        assertEquals(1F, eventDto.getLocation().getLat());
        assertEquals(2F, eventDto.getLocation().getLon());
        assertEquals(true, eventDto.getRequestModeration());
        assertEquals("annotation", eventDto.getAnnotation());
        assertEquals(MIN_TIME, eventDto.getEventDate());
        assertEquals(MIN_TIME, eventDto.getPublishedOn());
        assertEquals("description", eventDto.getDescription());
        assertEquals("title", eventDto.getTitle());
        assertEquals(EventState.PENDING, eventDto.getState());
        assertEquals(1L, eventDto.getCategory().getId());
        assertEquals("category", eventDto.getCategory().getName());
        assertEquals(1L, eventDto.getInitiator().getId());
        assertEquals("user", eventDto.getInitiator().getName());
        assertEquals(1L, eventDto.getViews());
    }

    @Test
    void update() {
        //given
        Event event = setupEvent();
        EventInputDto inputDto = EventInputDto.builder()
                .title("new_title")
                .stateAction(EventStateAction.REJECT_EVENT)
                .location(EventInputDto.Location.builder().lat(3F).lon(4F).build())
                .build();
        Category categoryUpdate = Category.builder()
                .id(1L)
                .name("new_category")
                .build();
        //when
        mapper.update(inputDto, categoryUpdate, event, MIN_TIME);
        //then
        assertEquals(true, event.getPaid());
        assertEquals(3F, event.getLocationLat());
        assertEquals(4F, event.getLocationLon());
        assertEquals(true, event.getRequestModeration());
        assertEquals("annotation", event.getAnnotation());
        assertEquals(MIN_TIME, event.getEventDate());
        assertEquals("description", event.getDescription());
        assertEquals("new_title", event.getTitle());
        assertEquals(EventState.CANCELED, event.getState());
        assertEquals(categoryUpdate, event.getCategory());
        assertEquals(initiator, event.getInitiator());


    }

    private Event setupEvent() {
        return Event.builder()
                .paid(true)
                .locationLat(1F)
                .locationLon(2F)
                .title("title")
                .requestModeration(true)
                .annotation("annotation")
                .initiator(initiator)
                .category(category)
                .eventDate(MIN_TIME)
                .description("description")
                .requestModeration(true)
                .publishedOn(MIN_TIME)
                .state(EventState.PENDING)
                .confirmedRequests(1L)
                .views(1L)
                .build();
    }

    private EventInputDto setupInputDto() {
        return EventInputDto.builder()
                .paid(true)
                .location(EventInputDto.Location.builder().lat(1F).lon(2F).build())
                .title("title")
                .requestModeration(true)
                .annotation("annotation")
                //TODO
                .eventDate(MIN_TIME.format(Constants.DATE_TIME_FORMATTER))
                .description("description")
                .requestModeration(true)
                .build();
    }
}