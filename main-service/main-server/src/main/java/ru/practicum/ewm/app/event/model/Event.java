package ru.practicum.ewm.app.event.model;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import ru.practicum.ewm.app.category.model.Category;
import ru.practicum.ewm.app.compilation.model.Compilation;
import ru.practicum.ewm.app.user.model.User;
import ru.practicum.ewm.common.utils.EventState;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.ManyToMany;
import javax.persistence.JoinColumn;
import javax.persistence.Enumerated;
import javax.persistence.Transient;
import javax.persistence.EnumType;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;


/**
 * Событие - предмет управления приложения
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"id"}, callSuper = false)
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String annotation;

    private String description;

    @Column(name = "event_date")
    private LocalDateTime eventDate;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "initiator_id", updatable = false)
    private User initiator;

    private Float locationLat;

    private Float locationLon;

    @Column(name = "created", updatable = false)
    private LocalDateTime createdOn;

    private Boolean paid;

    @Column(name = "moderation")
    private Boolean requestModeration;

    @Column (name = "part_limit")
    Long participantLimit;

    @Column(name = "published")
    private LocalDateTime publishedOn;

    @Enumerated(value = EnumType.STRING)
    private EventState state;

    @Transient
    private Long confirmedRequests;

    @Transient
    private Long views;

    @ManyToMany(mappedBy = "events")
    Set<Compilation> compilations;

    @PrePersist
    @PreUpdate
    void setTimestamps() {
        if (createdOn == null) setCreatedOn(LocalDateTime.now());
        if (state == EventState.PUBLISHED) setPublishedOn(LocalDateTime.now());
    }
}